package stelacura

import ktx.app.KtxGame
import ktx.app.KtxScreen

class Core : KtxGame<KtxScreen>() {

    override fun create() {
        val splashScreen = SplashScreen()
        addScreen(splashScreen)
        addScreen(Observatory1Screen())
        setScreen<Observatory1Screen>()
    }

}