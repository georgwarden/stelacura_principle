package stelacura

import com.badlogic.gdx.math.Vector2

interface InputSource {

    /**
     * Этот метод должен вернуть направление, в котором наклонён стик/которое кодирует комбинация клавиш на клавиатуре
     */
    fun pollDirection(): Vector2

    fun onActionClicked(f: () -> Unit)
    fun onJumpClicked(f: () -> Unit)
    fun onInventoryClicked(f: () -> Unit)

}