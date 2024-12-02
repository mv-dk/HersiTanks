package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

class TankInfoBar(val parent: IGameScene, val position: Pos2D) : GameObject2(parent, position) {
    val stroke = BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    val font = Font("Helvetica", Font.PLAIN, 18)
    val purple = Color(30, 30, 80)
    val darkPurple = Color(20, 20, 40)


    override fun update() {

    }

    override fun draw(g: Graphics2D) {
        g.color = Color.DARK_GRAY
        g.fillRect(position.x.toInt(), position.y.toInt(), parent.width, 32)

        g.stroke = stroke
        g.color = Color.YELLOW
        g.font = font
        g.drawString("Power: ${GameController.getCurrentTank().power}", position.x.toInt() + 16, position.y.toInt() + 22)
        g.drawString("Angle: ${GameController.getCurrentTank().angle.toInt()}", position.x.toInt() + 286, position.y.toInt() + 22)
        g.drawString("Energy: ${GameController.getCurrentTank().energy}", position.x.toInt() + 516, position.y.toInt() + 22)
    }

}