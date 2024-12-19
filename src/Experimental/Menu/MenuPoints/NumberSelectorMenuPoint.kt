package Experimental.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject
import Game.GameController
import java.awt.Graphics2D

class NumberSelectorMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D,
    var numberValue: Int,
    val min: Int,
    val max: Int,
    val step: Int = 1,
    val onChange: (old: Int, new: Int) -> Unit
):
    MenuPointGameObject(text, parent, position) {

    override fun draw(g: Graphics2D) {
        if (selected) {
            g.color = selectedColor
            g.font = selectedFont
            g.drawString("$text: < $numberValue >", position.x.toFloat(), position.y.toFloat())
        } else {
            g.color = unselectedColor
            g.font = unselectedFont
            g.drawString("$text: $numberValue", position.x.toFloat(), position.y.toFloat())
        }
    }

    fun increase() {
        val oldValue = numberValue
        if (numberValue < max) numberValue += step
        onChange(oldValue, numberValue)
    }

    fun decrease() {
        val oldValue = numberValue
        if (numberValue > min) numberValue -= step
        onChange(oldValue, numberValue)
    }
}