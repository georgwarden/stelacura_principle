package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxScreen
import ktx.app.use

class SplashScreen2 : KtxScreen {

    private val inventory = Inventory()
    private val batch = SpriteBatch()

    init {
        inventory.addNote("Egor pidor")
        inventory.addNote("Vrode daje")
        inventory.addNote("Rabotaet")
        inventory.addNote("Check")

        inventory.setPosition(100f, 100f)
        inventory.fuck = true

        Gdx.app.log("Test", "${Gdx.app.graphics.width} x ${Gdx.app.graphics.height}")
    }

    override fun show() {
        // Prepare your screen here.
    }

    override fun render(delta: Float) {
        batch.use { b ->
            inventory.draw(b, 1f)
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
    }
}