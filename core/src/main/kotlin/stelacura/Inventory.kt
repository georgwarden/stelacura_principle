package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Actor

class Inventory : Actor() {
    private var notes = ArrayList<String>()
    var fuck = false

    private val paperSprite = Sprite(Texture(Gdx.files.internal("notebook.png")))

    private val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/TangoDi.ttf"))
    private val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().also {
        it.size = 15
        it.color = Color.BLACK
    }
    private val font = generator.generateFont(parameter)

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (fuck) {
            paperSprite.setPosition(this.x, this.y)
            paperSprite.setSize(865f / 2f, 606f / 2f)
            paperSprite.draw(batch)
            notes.forEachIndexed { index, note ->
                font.draw(batch, note, this.x + 55f, this.y + 292f - 27f * index)
            }
        }
    }

    fun addNote(note: String) {
        this.notes.add(note)
    }
}