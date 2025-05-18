package Game.TerrainScene

import Engine.*
import Game.GameController
import gameResX
import gameResY
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import kotlin.math.roundToInt

class TankInfoBar(parent: IGameScene, position: Pos2D) : GameObject2(parent, position) {
    val stroke = BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    val font = Font("Helvetica", Font.PLAIN, 18)
    val debugFont = Font("Helvetica", Font.PLAIN, 10)
    val purple = Color(30, 30, 80)
    val darkPurple = Color(20, 20, 40)

    init {
        drawOrder = 100
    }

    override fun update() {

    }

    override fun draw(g: Graphics2D) {
        drawAsHud(g) {
            g.color = Color.DARK_GRAY
            g.fillRect(position.x.toInt(), position.y.toInt(), parent.width, 32)

            g.stroke = stroke
            g.color = Color.WHITE
            g.font = font
            val currentPlayer = GameController.getCurrentPlayer()
            val currentTank = currentPlayer?.tank
            if (currentPlayer != null && currentTank != null) {
                g.drawString(
                    "Power: ${currentTank.power}",
                    position.x.toInt() + 16,
                    position.y.toInt() + 22
                )
                g.drawString(
                    "Fuel: ${currentTank.fuel.roundToInt()} L",
                    position.x.toInt() + 120,
                    position.y.toInt() + 22
                )
                g.drawString(
                    "\$${currentPlayer.money}",
                    position.x.toInt() + 420,
                    position.y.toInt() + 22
                )
                g.drawString(
                    "Energy: ${currentTank.energy}",
                    position.x.toInt() + 516,
                    position.y.toInt() + 22
                )

                val nameWidth = g.fontMetrics.stringWidth(currentPlayer.name)
                // shadow
                g.color = Color.BLACK
                g.drawString(currentPlayer.name, (gameResX / 2 - nameWidth / 2) + 1, position.y.toInt() + 22 + 1)

                g.color = currentPlayer.color
                g.drawString(currentPlayer.name, gameResX / 2 - nameWidth / 2, position.y.toInt() + 22)

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