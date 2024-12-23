package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

class WeaponBar(parent: IGameScene, position: Pos2D) : GameObject2(parent, position) {
    val stroke = BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    val purple = Color(30, 30, 80)
    val darkPurple = Color(20, 20, 40)

    override fun update() {

    }

    override fun draw(g: Graphics2D) {
        g.color = Color.DARK_GRAY
        g.fillRect(position.x.toInt(), position.y.toInt(), parent.width, 32)

        g.stroke = stroke
        var i = 0
        var idx = 0
        for (weapon in Weapon.allWeapons) {
            if (idx == GameController.getCurrentPlayersTank()?.activeWeaponIdx) {
                g.color = Color.RED
            } else {
                g.color = darkPurple
            }
            g.drawRect((position.x + i).toInt(), position.y.toInt(), 32, 32)
            weapon.drawIcon(g, (position.x + i).toInt(), position.y.toInt())
            i += 35
            idx += 1
        }
    }
}