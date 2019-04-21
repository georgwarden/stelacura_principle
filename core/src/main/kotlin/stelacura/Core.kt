package stelacura

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen

class Core : KtxGame<KtxScreen>() {

    private lateinit var shapeRenderer: ShapeRenderer
    lateinit var curtain: CurtainActor

    override fun create() {
        shapeRenderer = ShapeRenderer()
        curtain = CurtainActor(shapeRenderer)

        addScreen(SplashScreen(this))
        addScreen(Observatory1Screen(this))
        addScreen(DesertScreen(this))

        goTo<DesertScreen>()
    }

    inline fun <reified T : KtxScreen> goTo() {
        setScreen<T>()
    }

}

class CurtainActor(private val renderer: ShapeRenderer) : Actor() {

    init {
        renderer.color = Color.BLACK
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color.a = this.color.a
        renderer.rect(0f, 0f, 1280f, 720f)
        renderer.end()
    }

}