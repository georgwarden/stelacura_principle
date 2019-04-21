package stelacura

import ktx.app.KtxGame
import ktx.app.KtxScreen

class Core : KtxGame<KtxScreen>() {

    override fun create() {
        val splashScreen = SplashScreen()
        addScreen(splashScreen)
        addScreen(Observatory1Screen(this))
        setScreen<Observatory1Screen>()
    }

    inline fun <reified T : KtxScreen> goTo() {
        setScreen<T>()
    }

}