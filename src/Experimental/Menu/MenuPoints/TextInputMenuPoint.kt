package Experimental.Menu.MenuPoints

import Engine.GameRunner
import Engine.GameWindow
import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject
import java.awt.Color
import java.awt.Graphics2D

class TextInputMenuPoint(
    val label: String,
    parent: IGameScene,
    override var position: Pos2D,
    initialTextValue: String,
    val maxLength: Int,
    val coloredBorder: Color? = null,
    initialFontSize : Int = 24
) : MenuPointGameObject(label, parent, cursor = true, fontSize = initialFontSize){
    var lighterColoredBorder = if (coloredBorder == null) null else Color(
        Math.min(coloredBorder?.red?.plus(100) ?: 255, 255),
        Math.min(coloredBorder?.green?.plus(100) ?: 255, 255),
        Math.min(coloredBorder?.blue?.plus(100) ?: 255, 255))
    var textValue = initialTextValue
        set(value) {
            text = "$label: $value"
            field = value
        }

    init {
        textValue = initialTextValue
    }

    override fun draw(g: Graphics2D) {
        if (coloredBorder != null) {
            g.color = if (selected) lighterColoredBorder else coloredBorder
            g.font = getFont()
            val width = g.fontMetrics.stringWidth(text)
            g.fillRect(position.x.toInt() - 3, (position.y - 3).toInt(), width, 6)
        }
        super.draw(g)
    }
}