package stelacura

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.scenes.scene2d.Actor

class MusicActor(val music: Music) : Actor() {

    override fun act(delta: Float) {
        super.act(delta)
        music.volume = this.color.a
    }

}