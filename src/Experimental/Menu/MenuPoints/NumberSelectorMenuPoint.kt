package Experimental.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject
import Game.GameController
import java.awt.Graphics2D

class NumberSelectorMenuPoint(
    val label: String,
    parent: IGameScene,
    var numberValue: Int,
    val min: Int,
    val max: Int,
    val step: Int = 1,
    val onChange: (old: Int, new: Int) -> Unit
):
    MenuPointGameObject("$label: $numberValue", parent) {

    init {
        super.text = "$label: $numberValue"
    }

    override fun onSelected() {
        super.text = "$label: <$numberValue>"
    }

    override fun onDeselected() {
        super.text = "$label: $numberValue"
    }

    fun increase() {
        val oldValue = numberValue
        if (numberValue < max) numberValue += step
        super.text = "$label: <$numberValue>"
        onChange(oldValue, numberValue)
    }

    fun decrease() {
        val oldValue = numberValue
        if (numberValue > min) numberValue -= step
        super.text = "$label: <$numberValue>"
        onChange(oldValue, numberValue)
    }
}