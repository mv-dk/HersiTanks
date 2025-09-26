package Game.Menu.MenuPoints

import Engine.IGameScene
import Engine.lighter
import java.awt.Color
import java.awt.Graphics2D

class TextInputMenuPoint(
    private val label: String,
    parent: IGameScene,
    initialTextValue: String,
    private val coloredBorder: Color? = null,
    initialFontSize : Int = 24,
    val maxTextLength: Int = Int.MAX_VALUE,
    val maxTextLengthIsMs: Boolean = false /* if false, max text length is the number of characters */
) : MenuPointGameObject(label, parent, cursor = true, fontSize = initialFontSize, onActivate = {}){
    private var lighterColoredBorder = coloredBorder?.lighter(100)
    var textValue = initialTextValue
        set(value) {
            if (maxTextLengthIsMs) {
                if (textWidthInMs(value) <= maxTextLength) {
                    text = "$label: $value"
                    field = value
                }
            } else {
                if (value.length <= maxTextLength) {
                    text = "$label: $value"
                    field = value
                }
            }
        }

    // Get an estimate of the string length expressed as the number of M-characters.
    fun textWidthInMs(value: String): Float {
        val smallMs = value.count{ it == 'm'}
        val bigMs = value.count { it == 'M' || it == 'Æ' }
        val bigNs = value.count { it == 'N' || it == 'D' || it == 'U' }
        val ns = value.count { it == 'n' || it == 'k' || it == 'K' || it == 'R' || it == 'a'}
        val rs = value.count { it == 'r' }
        val mids = value.count { it == 'f' }
        val smalls = value.count { it == 'l' || it == 'i' || it == 'j' || it == '\'' || it == '.' || it == ',' || it == '!'}
        val size = value.count()
        val others = size - smallMs - bigMs - bigNs - rs - ns - mids - smalls
        val width = smalls * 0.3f +
                    mids * 0.4f +
                    rs * 0.5f +
                    others * 0.65f +
                    ns * 0.7f +
                    bigNs * 0.8f +
                    bigMs +
                    smallMs * 1.1f
        return width
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