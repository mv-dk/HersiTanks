package Experimental.TerrainScene

import Engine.GameObject2
import Engine.GameRunner
import Engine.IGameScene
import Engine.Pos2D
import Game.GameController
import gameResX
import gameResY
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

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
        val oldTranslationX = g.transform.translateX
        val oldTranslationY = g.transform.translateY
        g.translate(-oldTranslationX, -oldTranslationY)
        g.color = Color.DARK_GRAY
        g.fillRect(position.x.toInt(), position.y.toInt(), parent.width, 32)

        g.stroke = stroke
        g.color = Color.WHITE
        g.font = font
        val currentTank = GameController.getCurrentPlayersTank()
        if (currentTank != null) {
            g.drawString(
                "Power: ${currentTank.power}",
                position.x.toInt() + 16,
                position.y.toInt() + 22
            )
            g.drawString(
                "Angle: ${currentTank.angle.toInt()}",
                position.x.toInt() + 120,
                position.y.toInt() + 22
            )
            g.drawString(
                "\$${GameController.getCurrentPlayer().money}",
                position.x.toInt() + 420,
                position.y.toInt() + 22
            )
            g.drawString(
                "Energy: ${currentTank.energy}",
                position.x.toInt() + 516,
                position.y.toInt() + 22
            )
            val player = GameController.getCurrentPlayer()

            val nameWidth = g.fontMetrics.stringWidth(player.name)
            // shadow
            g.color = Color.BLACK
            g.drawString(player.name, (gameResX / 2 - nameWidth/2) + 1, position.y.toInt() + 22 + 1)

            g.color = player.color
            g.drawString(player.name, gameResX / 2 - nameWidth/2, position.y.toInt() + 22)

            if (GameRunner.debug) {
                g.color = Color.RED
                g.font = debugFont
                g.drawString("GameObjects: ${parent.gameObjectsCount()}, wind: ${(GameController.wind * 100).toInt()/100.0}", 0, gameResY - 10)
                g.drawString("RenderBuffer [ms]: ${GameController.renderBufferTime}", 250, gameResY - 10)
                g.drawString("RenderScreen [ms]: ${GameController.renderScreenTime}", 400, gameResY - 10)
                g.drawString("Update [ms]: ${GameController.updateTime}", 520, gameResY - 10)
            }
        }
        g.translate(oldTranslationX, oldTranslationY)
    }
}