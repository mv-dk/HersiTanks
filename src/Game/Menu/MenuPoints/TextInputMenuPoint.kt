package Game.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import Engine.lighter
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.min

class TextInputMenuPoint(
    private val label: String,
    parent: IGameScene,
    initialTextValue: String,
    private val coloredBorder: Color? = null,
    initialFontSize : Int = 24,
    val maxTextLength: Int = Int.MAX_VALUE
) : MenuPointGameObject(label, parent, cursor = true, fontSize = initialFontSize, onActivate = {}){
    private var lighterColoredBorder = coloredBorder?.lighter(100)
    var textValue = initialTextValue
        set(value) {
            if (value.length <= maxTextLength) {
                text = "$label: $value"
                field = value
            }
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