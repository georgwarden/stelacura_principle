package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.World
import com.brashmonkey.spriter.Player
import com.brashmonkey.spriter.SCMLReader
import com.uwsoft.editor.renderer.utils.LibGdxDrawer
import com.uwsoft.editor.renderer.utils.LibGdxLoader
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.box2d.body
import ktx.box2d.filter
import ktx.box2d.ropeJointWith
import com.badlogic.gdx.utils.Array as GdxArray

class Observatory1Screen : KtxScreen {

    private val pScale = 0.15f
    private val heroHalfW = 40f
    private val heroHalfH = 123f

    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    private val inputSource: InputSource = ControllerController()

    private val handle = Gdx.files.internal("walk_ani/stelacura_walks.scml")
    private val data = SCMLReader(handle.read()).data
    private val loader = LibGdxLoader(data).also { it.load(handle.file()) }
    private val drawer = LibGdxDrawer(loader, shapeRenderer)
    private val player = Player(data.getEntity(0)).apply {
        scale = pScale
    }
    private val stillPlayer = Sprite(Texture(Gdx.files.internal("stelacura.png"))).apply {
        setScale(pScale)
    }

    private val world = World(Vector2(0.0f, -50.0f), true)
    private val camera = OrthographicCamera().also {
        it.position.set(0f, 0f, 0f)
        it.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }
    private val renderer = Box2DDebugRenderer()

    private val hero = world.body {
        type = BodyDef.BodyType.DynamicBody
        box(heroHalfW * 2, heroHalfH * 2) {
            density = 0.0f
            friction = 0.0f
            restitution = 0f
        }
        position.set(0f, 100f)
        fixedRotation = true
    }
    private val ground = world.body {
        type = BodyDef.BodyType.StaticBody
        box(1280f * 2) {
            density = 0f
            friction = 0f
            restitution = 0f
            filter {
                categoryBits = Categories.Bedrock
            }
        }
    }

    init {
        Controllers.addListener(inputSource as ControllerListener)
        inputSource.onJumpClicked {
            hero.applyLinearImpulse(Vector2(0f, 3000f), Vector2(heroHalfW, heroHalfH), true)
        }
    }

    private var isDirectedRight = true
    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f, 1f)
        renderer.render(world, camera.combined)
        world.step(1/45f, 6, 2)
        camera.position.set(hero.position.x, 360f, 0f)
        camera.update()

        val input = inputSource.pollDirection()

        val move = input.x != 0f

        hero.applyForceToCenter(Vector2(input.x * 100f, 0f), true)

        if (input.x > 0) {
            if (!isDirectedRight) {
                isDirectedRight = true
                player.flipX()
                stillPlayer.flip(true, false)
            }
        }

        if (input.x < 0) {
            if (isDirectedRight) {
                isDirectedRight = false
                player.flipX()
                stillPlayer.flip(true, false)
            }
        }

        val drawCoords = camera.project(Vector3(hero.position.x, hero.position.y, 0f))

        player.setPosition(drawCoords.x, drawCoords.y)
        player.update()
        stillPlayer.setOrigin(drawCoords.x + heroHalfW, drawCoords.y - heroHalfH)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        for (i in -100..100) {
            val bottom = camera.project(Vector3(i * 100f, 0f, 0f))
            shapeRenderer.line(bottom.x, 0f, bottom.x, 720f)
        }
        shapeRenderer.end()

        batch.begin()
        if (move) {
            drawer.beforeDraw(player, batch)
            drawer.draw(player)
        } else {
            stillPlayer.draw(batch)
        }
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }

}