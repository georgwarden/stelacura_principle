package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeType
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.app.KtxScreen
import ktx.app.clearScreen

class SplashScreen(private val core: Core) : KtxScreen {

    private val WORLD_WIDTH = 1920f
    private val WORLD_HEIGHT = 4320f

    private val actor = Actor()
    private val logoActor = LogoActor()
    private val playActor = PlayActor()
    private val curtainActor = CurtainActor(ShapeRenderer())

    private val skySprite = Sprite(Texture(Gdx.files.internal("sky2.png")))
    private val cam = OrthographicCamera(1920f, 1080f)
    private val batch = SpriteBatch()

    private val controllerController = ControllerController()

    init {
        controllerController.onStartClicked {
            curtainActor.addAction(
                    Actions.sequence(
                            Actions.fadeIn(1.5f),
                            Actions.run { core.goTo<Observatory1Screen>() }
                    )
            )
        }

        skySprite.setPosition(0f, 0f)
        skySprite.setSize(WORLD_WIDTH, WORLD_HEIGHT)

        logoActor.setPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT - 540f)
        playActor.setPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT - 640f)
        playActor.setSize(WORLD_WIDTH, WORLD_HEIGHT)

        actor.setPosition(0f, 0f)
        actor.setSize(WORLD_WIDTH, 1080f)
        logoActor.color.a = 0f
        playActor.color.a = 0f

        curtainActor.addAction(
                Actions.sequence(
                        Actions.fadeOut(1.5f),
                        Actions.run {
                            actor.addAction(
                                    Actions.sequence(
                                            Actions.moveTo(0f, 3240f, 1.7f, Interpolation.exp5),
                                            Actions.run {
                                                logoActor.addAction(
                                                        Actions.sequence(
                                                                Actions.fadeIn(1f),
                                                                Actions.run { playActor.addAction(Actions.fadeIn(1.5f)) },
                                                                Actions.run { Controllers.addListener(controllerController) }
                                                        )
                                                )
                                            }
                                    )
                            )
                        }
                )
        )

        cam.position.set(actor.width / 2f, actor.height / 2f, 0f)
        cam.update()
    }

    override fun show() {
        // Prepare your screen here.
    }

    override fun render(delta: Float) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        //handleInput()
        clearScreen(0f, 0f, 0f, 1f)
        actor.act(delta)
        logoActor.act(delta)
        playActor.act(delta)
        curtainActor.act(delta)
        cam.position.set(actor.width / 2f + actor.x, actor.height / 2f + actor.y, 0f)
        cam.update()
        batch.projectionMatrix = cam.combined

        batch.begin()
        skySprite.draw(batch)
        logoActor.draw(batch, 1.0f)
        playActor.draw(batch, 1.0f)
        if (curtainActor.color.a != 0.0f) curtainActor.draw(null, 1.0f)
        batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            actor.addAction(Actions.moveTo(0f, 0f, 1.7f, Interpolation.exp5))
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            actor.addAction(Actions.delay(2f, Actions.moveTo(0f, 3240f, 1.7f, Interpolation.exp5)))
        }
    }

    override fun resize(width: Int, height: Int) {
        // Resize your screen here. The parameters represent the new window size.
    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
    }

    override fun dispose() {
        // Destroy screen's assets here.
        skySprite.texture.dispose()
        batch.dispose()
    }
}

class LogoActor : Actor() {
    private val logoSprite = Sprite(Texture(Gdx.files.internal("logo.png")))

    override fun draw(batch: Batch, parentAlpha: Float) {
        logoSprite.draw(batch)
    }

    override fun act(delta: Float) {
        super.act(delta)
        logoSprite.setPosition(this.x - logoSprite.width / 2, this.y)
        logoSprite.setAlpha(this.color.a)
        logoSprite.setScale(1.5f)
    }
}

class PlayActor : Actor() {

    private val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"))
    private val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().also {
        it.size = 48
        it.borderStraight = true
        it.color = Color.valueOf("B3D3FFFF")
    }
    private val font = generator.generateFont(parameter)
    private val glyph = GlyphLayout(font, "PRESS START")

    override fun draw(batch: Batch, parentAlpha: Float) {
        font.draw(batch, "PRESS START", this.x - glyph.width / 2, this.y - 100f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        font.color.a = this.color.a
    }
}