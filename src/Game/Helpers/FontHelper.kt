package Game.Helpers

import Game.TerrainScene.TankInfoBar
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File
import java.net.URI

object FontHelper {
    var balooFont: Font? = null

    init {
        val ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        if (balooFont == null) {
            balooFont = Font.createFont(
                Font.TRUETYPE_FONT, File(
                    TankInfoBar::class.java.classLoader.getResource(
                        "BalooBhaijaan-Regular.ttf"
                    )?.toURI() ?: URI.create("BalooBhaijaan-Regular.ttf")
                )
            )
            ge.registerFont(balooFont)
        }
    }
}