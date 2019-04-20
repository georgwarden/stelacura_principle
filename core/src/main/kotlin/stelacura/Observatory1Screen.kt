package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
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

    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    private val handle = Gdx.files.internal("walk_ani/stelacura_walks.scml")
    private val data = SCMLReader(handle.read()).data
    private val loader = LibGdxLoader(data).also { it.load(handle.file()) }
    private val drawer = LibGdxDrawer(loader, shapeRenderer)
    private val player = Player(data.getEntity(0)).apply {
        scale = 0.08f
    }

    private val world = World(Vector2(0.0f, -10.0f), true)
    private val camera = OrthographicCamera().also {
        it.position.set(0f, 0f, 0f)
        it.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }
    private val renderer = Box2DDebugRenderer()

    private val hero = world.body {
        type = BodyDef.BodyType.DynamicBody
        box(50f, 100f) {
            density = 0.1f
            friction = 0.5f
            restitution = 0f
        }
        position.set(0f, 100f)
        fixedRotation = true
    }
    private val cameraAnchor = world.body {
        type = BodyDef.BodyType.DynamicBody
        box(1280f, 720f) {
            filter {
                categoryBits = Categories.Camera
                maskBits = Categories.Bedrock
            }
        }
    }.also { anchor ->
        anchor.ropeJointWith(hero) {
            this.maxLength = 360f
        }
    }
    private val ground = world.body {
        type = BodyDef.BodyType.StaticBody
        box(1280f) {
            density = 0f
            friction = 0f
            restitution = 0f
            filter {
                categoryBits = Categories.Bedrock
            }
        }
    }

    private var isDirectedRight = true
    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f, 1f)
        renderer.render(world, camera.combined)
        world.step(1/45f, 6, 2)
        camera.position.set(cameraAnchor.position, 0f)
        camera.update()

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            hero.applyLinearImpulse(Vector2(100f, 0f), Vector2(25f, 50f), true)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (!isDirectedRight) {
                isDirectedRight = true
                player.flipX()
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            hero.applyLinearImpulse(Vector2(-100f, 0f), Vector2(25f, 50f), true)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (isDirectedRight) {
                isDirectedRight = false
                player.flipX()
            }
        }

        val drawCoords = camera.project(Vector3(hero.position.x, hero.position.y, 0f))
        player.setPosition(drawCoords.x, drawCoords.y)
        player.update()

        batch.begin()
        drawer.beforeDraw(player, batch)
        drawer.draw(player)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }

}