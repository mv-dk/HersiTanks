package Game.Helpers

import java.awt.Font
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
}