package stelacura

import ktx.app.KtxGame
import ktx.app.KtxScreen

class Core : KtxGame<KtxScreen>() {

    override fun create() {
        addScreen(SplashScreen())
        addScreen(Observatory1Screen(this))
        addScreen(DesertScreen())

        setScreen<SplashScreen>()
    }

    inline fun <reified T : KtxScreen> goTo() {
        setScreen<T>()
    }

}