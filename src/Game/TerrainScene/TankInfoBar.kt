package Game.TerrainScene

import Engine.*
import Game.GameController
import Game.Helpers.FontHelper
import gameResX
import gameResY
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import kotlin.math.roundToInt

class TankInfoBar(parent: IGameScene, position: Pos2D) : GameObject2(parent, position) {
    val stroke = BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    val font = FontHelper.balooFont?.deriveFont(Font.PLAIN, 18f) ?: Font("Helvetica", Font.PLAIN, 18)
    val debugFont = Font("Helvetica", Font.PLAIN, 10)

    init {
        drawOrder = 100
    }

    override fun update() {

    }

    private fun drawStringWithShadow(
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

    override fun draw(g: Graphics2D) {
        drawAsHud(g) {
            g.stroke = stroke
            g.color = Color.WHITE
            g.font = font
            val currentPlayer = GameController.getCurrentPlayer()
            val currentTank = currentPlayer?.tank
            if (currentPlayer != null && currentTank != null) {
                drawStringWithShadow(
                    g,
                    "Power: ${currentTank.power}",
                    position.x.toInt() + 16,
                    position.y.toInt() + 22
                )
                drawStringWithShadow(
                    g,
                    "Fuel: ${currentTank.fuel.roundToInt()} L",
                    position.x.toInt() + 120,
                    position.y.toInt() + 22
                )
                drawStringWithShadow(
                    g,
                    "\$${currentPlayer.money}",
                    position.x.toInt() + 420,
                    position.y.toInt() + 22
                )
                drawStringWithShadow(
                    g,
                    "Energy: ${currentTank.energy}",
                    position.x.toInt() + 516,
                    position.y.toInt() + 22
                )

                val nameWidth = g.fontMetrics.stringWidth(currentPlayer.name)

                g.color = currentPlayer.color
                drawStringWithShadow(g, currentPlayer.name, gameResX / 2 - nameWidth / 2, position.y.toInt() + 22)

                if (GameRunner.debug) {
                    g.color = Color.RED
                    g.font = debugFont
                    g.drawString(
                        "GameObjects: ${parent.gameObjectsCount()}, wind: ${(GameController.wind * 100).toInt() / 100.0}",
                        0,
                        gameResY - 10
                    )
                    g.drawString("RenderBuffer [ms]: ${GameController.renderBufferTime}", 250, gameResY - 10)
                    g.drawString("RenderScreen [ms]: ${GameController.renderScreenTime}", 400, gameResY - 10)
                    g.drawString("Update [ms]: ${GameController.updateTime}", 520, gameResY - 10)
                }
            }
        }
    }
}

