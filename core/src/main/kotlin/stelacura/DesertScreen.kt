package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.brashmonkey.spriter.Player
import com.brashmonkey.spriter.SCMLReader
import com.uwsoft.editor.renderer.utils.LibGdxDrawer
import com.uwsoft.editor.renderer.utils.LibGdxLoader
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.box2d.body

class DesertScreen(private val core: Core) : KtxScreen {

    private val pScale = 0.15f
    private val heroHalfW = 40f
    private val heroHalfH = 123f
    private val levelLength = 1280 * 4f

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

    private val world = World(Vector2(0.0f, -50.0f), true)
    private val camera = OrthographicCamera().also {
        it.position.set(0f, 0f, 0f)
        it.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }
    private val renderer = Box2DDebugRenderer()

    private val spaceBackground = Texture(Gdx.files.internal("sky2.png"))
    private val background = Sprite(Texture(Gdx.files.internal("dst_bg.png"))).also {
        it.setPosition(0f, 0f)
    }

    private val themeActor = MusicActor(Gdx.audio.newMusic(Gdx.files.internal("desert_theme.mp3"))).also { it.color.a = 0.5f }
    private val curtainActor = CurtainActor(ShapeRenderer())
    private val steps = Gdx.audio.newSound(Gdx.files.internal("sounds/sand_steps.mp3"))

    private val hero = world.body {
        type = BodyDef.BodyType.DynamicBody
        box(heroHalfW * 2, heroHalfH * 2) {
            density = 0.0f
            friction = 0.0f
            restitution = 0f
        }
        position.set(100f, heroHalfH + 60f)
        this.linearDamping = 1.5f
        fixedRotation = true
    }

    private val ground = world.body {
        type = BodyDef.BodyType.StaticBody
        box(levelLength) {
            density = 0f
            friction = 0f
            restitution = 0f
        }
        position.set(levelLength / 2, 60f)
    }
    private val leftWall = world.body {
        type = BodyDef.BodyType.StaticBody
        box(height = 720f) {
        }
        position.set(0f, 360f)
    }
    private val rightWall = world.body {
        type = BodyDef.BodyType.StaticBody
        box(height = 720f) {
        }
        position.set(levelLength, 360f)
    }

    private val telescope = world.body {
        type = BodyDef.BodyType.StaticBody
        userData = "telescope"
        box(width = 100f, height = 200f) {
            isSensor = true
        }
        position.set(levelLength - 80f, 0f)
    }
    private val telescopeModel = Sprite(Texture(Gdx.files.internal("telescope.png")))

    private var currentlyInteractable: String? = null
    private var interactionActions = mapOf(
            "telescope" to {
                themeActor.addAction(
                        Actions.sequence(
                                Actions.fadeOut(1.5f),
                                Actions.run { themeActor.music.stop() },
                                Actions.run {
                                    curtainActor.addAction(
                                            Actions.sequence(
                                                    Actions.fadeIn(1.5f),
                                                    Actions.run { core.goTo<DesertScreen>() }
                                            )
                                    )
                                }
                        )
                )
            }
    )

    private val interactionHint = Texture(Gdx.files.internal("interaction_hint.png"))
    private var interactionHintVisible = false
    private val interactionHintPosition = Vector2()

    init {
        curtainActor.color.a = 1.0f
        Controllers.addListener(inputSource as ControllerListener)
        inputSource.onJumpClicked {
            hero.applyLinearImpulse(Vector2(0f, 3000f), Vector2(heroHalfW, heroHalfH), true)
        }
        inputSource.onActionClicked {
            currentlyInteractable?.let {
                interactionActions[it]
            }?.invoke()
        }

        world.setContactListener(object : ContactListener {
            override fun beginContact(contact: Contact) {
                val bA = contact.fixtureA.body
                val bB = contact.fixtureB.body

                fun ap(interactable: Body) {
                    currentlyInteractable = interactable.userData as String
                    val v0 = interactable.position
                    interactionHintPosition.set(v0.x - 39f, heroHalfH * 2 + 40f)
                    interactionHintVisible = true
                }

                when {
                    bA == hero && bB.userData in interactionActions -> ap(bB)
                    bB == hero && bA.userData in interactionActions -> ap(bA)
                }

            }

            override fun endContact(contact: Contact) {
                val bA = contact.fixtureA.body
                val bB = contact.fixtureB.body

                fun ap() {
                    currentlyInteractable = null
                    interactionHintVisible = false
                }

                when {
                    bA == hero && bB.userData in interactionActions -> ap()
                    bB == hero && bA.userData in interactionActions -> ap()
                }
            }

            override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
            }

            override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
            }

        })
    }

    override fun show() {
        curtainActor.addAction(Actions.fadeOut(1.5f))
        themeActor.music.play()
    }

    private var stepsId: Long? = null
    private var isDirectedRight = true
    override fun render(delta: Float) {
        clearScreen(0.3f, 0.3f, 0.3f, 1f)
        renderer.render(world, camera.combined)
        world.step(1 / 45f, 6, 2)
        camera.position.set((hero.position.x.takeIf { it > 640f } ?: 640f).takeIf { it < levelLength - 640f }
                ?: (levelLength - 640), 360f, 0f)
        camera.update()

        val input = inputSource.pollDirection()

        val move = input.x != 0f

        hero.applyForceToCenter(Vector2(input.x * 300f, 0f), true)

        if (input.x > 0) {
            if (!isDirectedRight) {
                isDirectedRight = true
                player.flipX()
            }
        }

        if (input.x < 0) {
            if (isDirectedRight) {
                isDirectedRight = false
                player.flipX()
            }
        }

        if (move) {
            if (stepsId == null) {
                stepsId = steps.loop()
            }
        }

        if (!move) {
            player.time = 0
            stepsId?.let { steps.stop(it) }
            stepsId = null
        }

        val drawCoords = camera.project(Vector3(hero.position.x, hero.position.y, 0f))

        player.setPosition(drawCoords.x, drawCoords.y)
        player.update()

        curtainActor.act(delta)
        themeActor.act(delta)

        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        for (i in -100..100) {
            val bottom = camera.project(Vector3(i * 100f, 0f, 0f))
            shapeRenderer.line(bottom.x, 0f, bottom.x, 720f)
        }
        shapeRenderer.end()
*/
        batch.begin()
        batch.draw(spaceBackground, 0f, 0f)
        val bgc = camera.project(Vector3())
        batch.draw(background, bgc.x, 0f)
        if (interactionHintVisible) {
            val dc = camera.project(Vector3(interactionHintPosition, 0f))
            batch.draw(interactionHint, dc.x, dc.y)
        }
        drawer.beforeDraw(player, batch)
        drawer.draw(player)
        if (curtainActor.color.a != 0f) curtainActor.draw(null, 1f)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }

}