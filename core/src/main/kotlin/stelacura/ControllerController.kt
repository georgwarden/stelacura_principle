package stelacura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.math.round

typealias Listener = () -> Unit

class ControllerController : ControllerListener, InputSource {

    private val vector2 = Vector2()

    private var onAction: Listener? = null
    private var onInventory: Listener? = null
    private var onJump: Listener? = null
    private var onStart: Listener? = null

    override fun pollDirection(): Vector2 {
        return vector2.cpy()
    }

    override fun onActionClicked(f: () -> Unit) {
        onAction = f
    }

    override fun onJumpClicked(f: () -> Unit) {
        onJump = f
    }

    override fun onInventoryClicked(f: () -> Unit) {
        onInventory = f
    }

    override fun onStartClicked(f: () -> Unit) {
        onStart = f
    }

    override fun connected(controller: Controller?) {

    }

    // 0 - jump
    // 2 - action
    // 3 - inventory
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        Gdx.app.log("Controller", buttonCode.toString())
        when(buttonCode) {
            2 -> {
                //Gdx.app.log("Controller", "Action clicked!")
                onAction?.invoke()
            }

            3 -> {
                //Gdx.app.log("Controller", "Inventory clicked!")
                onInventory?.invoke()
            }

            0 -> {
                //Gdx.app.log("Controller", "Jump clicked!")
                onJump?.invoke()
            }
            7 -> {
                onStart?.invoke()
            }
        }
        return true
    }

    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        return true
    }

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
//        Gdx.app.log("Controller", "axisCode: $axisCode; value: $value")
        when(axisCode) {
            1 -> vector2.x = round(value)
            0 -> vector2.y = -round(value)
        }
        //Gdx.app.log("Controller", "$vector2")
        return true
    }

    override fun disconnected(controller: Controller?) {

    }

    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        return true
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        return true
    }

}