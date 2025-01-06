package Experimental.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.min

class TextInputMenuPoint(
    private val label: String,
    parent: IGameScene,
    override var position: Pos2D,
    initialTextValue: String,
    private val coloredBorder: Color? = null,
    initialFontSize : Int = 24
) : MenuPointGameObject(label, parent, cursor = true, fontSize = initialFontSize, onActivate = {}){
    private var lighterColoredBorder = if (coloredBorder == null) null else Color(
        min(coloredBorder.red.plus(100) ?: 255, 255),
        min(coloredBorder.green.plus(100) ?: 255, 255),
        min(coloredBorder.blue.plus(100) ?: 255, 255)
    )
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