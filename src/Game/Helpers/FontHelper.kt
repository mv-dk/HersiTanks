package Game.Helpers

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment

object FontHelper {
    var balooFont: Font? = null

    init {
        val ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        if (balooFont == null) {
            val inStream = FontHelper.javaClass.classLoader.getResourceAsStream("BalooBhaijaan-Regular.ttf")
            balooFont = Font.createFont(Font.TRUETYPE_FONT, inStream)
            ge.registerFont(balooFont)
        }
    }

    fun drawStringWithShadow(
        g: Graphics2D,
        text: String,
        x: Int,
        y: Int
    ) {
        val prevColor = g.color
        g.color = Color.BLACK
        g.drawString(text, x+1, y+1)
        g.color = prevColor
        g.drawString(text, x, y)
    }
}