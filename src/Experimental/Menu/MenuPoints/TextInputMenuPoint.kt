package Experimental.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject
import java.awt.Graphics2D

class TextInputMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D,
    textValue: String,
    val maxLength: Int
) : MenuPointGameObject(text, parent, position){
    private var tick = 0

    var textValue = textValue
        set(value) {
            if (value.length <= maxLength)
                field = value
        }

    override fun draw(g: Graphics2D) {
        if (selected) {
            g.color = selectedColor
            g.font = selectedFont
        } else {
            g.color = unselectedColor
            g.font = unselectedFont
        }
        tick = (tick+1) % 60
        g.drawString("$text: $textValue${if (selected && tick > 30) "|" else ""}", position.x.toFloat(), position.y.toFloat())
    }
}