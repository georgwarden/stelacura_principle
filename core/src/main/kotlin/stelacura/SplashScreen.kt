package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.app.KtxScreen

class SplashScreen : KtxScreen {

    private val WORLD_WIDTH = 1920f
    private val WORLD_HEIGHT = 4320f

    private val actor = Actor()
    private val logoActor = LogoActor()
    private val playActor = PlayActor()

    private val skySprite = Sprite(Texture(Gdx.files.internal("sky2.png")))
    private val cam = OrthographicCamera(1920f, 1080f)
    private val batch = SpriteBatch()

    init {
        skySprite.setPosition(0f,0f)
        skySprite.setSize(WORLD_WIDTH, WORLD_HEIGHT)

        logoActor.setPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT - 540f)
        playActor.setPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT - 640f)
        playActor.setSize(WORLD_WIDTH, WORLD_HEIGHT)

        actor.setPosition(0f, 0f)
        actor.setSize(WORLD_WIDTH, 1080f)
        logoActor.color.a = 0f
        playActor.color.a = 0f

        playActor.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                System.out.println("Clicked!")
                super.clicked(event, x, y)
            }
        })

        actor.addAction(Actions.sequence(Actions.delay(2f, Actions.moveTo(0f, 3240f, 1.7f, Interpolation.exp5)), Actions.run {
            logoActor.addAction(Actions.fadeIn(1f))
            playActor.addAction(Actions.fadeIn(1.5f))
        }))

        cam.position.set(actor.width / 2f, actor.height / 2f, 0f )
        cam.update()
    }

    override fun show() {
        // Prepare your screen here.
    }

    override fun render(delta: Float) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        //handleInput()
        actor.act(delta)
        logoActor.act(delta)
        playActor.act(delta)
        cam.position.set(actor.width / 2f + actor.x, actor.height / 2f + actor.y, 0f)
        cam.update()
        batch.projectionMatrix = cam.combined

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        skySprite.draw(batch)
        logoActor.draw(batch, 1.0f)
        playActor.draw(batch, 1.0f)
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
    private val logoSprite = Sprite(Texture(Gdx.files.internal("libgdx64.png")))

    override fun draw(batch: Batch, parentAlpha: Float) {
        logoSprite.draw(batch)
    }

    override fun act(delta: Float) {
        super.act(delta)
        logoSprite.setPosition(this.x, this.y)
        logoSprite.setAlpha(this.color.a)
    }
}

class PlayActor : Actor() {
    private val playSprite = Sprite(Texture(Gdx.files.internal("libgdx32.png")))

    override fun draw(batch: Batch?, parentAlpha: Float) {
        playSprite.draw(batch)
    }

    override fun act(delta: Float) {
        super.act(delta)
        playSprite.setPosition(this.x, this.y)
        playSprite.setAlpha((this.color.a))
    }
}