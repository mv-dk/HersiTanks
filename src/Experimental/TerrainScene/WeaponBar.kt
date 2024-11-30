package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

class WeaponBar(val parent: IGameScene, val position: Pos2D) : GameObject2(parent, position) {
    val stroke = BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    val purple = Color(30, 30, 80)
    val darkPurple = Color(20, 20, 40)
    val weaponOrder = (0..9).toList()

    override fun update() {

    }

    override fun draw(g: Graphics2D) {
        g.color = Color.DARK_GRAY
        g.fillRect(position.x.toInt(), position.y.toInt(), parent.width, 32)

        g.stroke = stroke
        var i = 0
        var idx = 0
        while (i < parent.width) {
            if (idx < weaponOrder.size) {
                drawWeapon(g, weaponOrder[idx], (position.x + i).toInt())
            }
            i += 35
            idx += 1
        }
    }

    fun drawWeapon(g: Graphics2D, n: Int, x: Int){
        val activeWeapon = GameController.getCurrentTank().activeWeapon
        g.color = if (activeWeapon == n) Color.RED else darkPurple
        g.drawRect(x, position.y.toInt(), 32, 32)
        when(n) {
            WEAPON_TINY_BOMB -> g.fillOval(x + 12, (position.y + 12).toInt(), 8, 8)
            WEAPON_BOMB -> g.fillOval(x + 10, (position.y + 10).toInt(), 12, 12)
            WEAPON_BIGGER_BOMB -> g.fillOval(x + 8, (position.y + 8).toInt(), 16, 16)
            WEAPON_BIGGEST_BOMB -> g.fillOval(x + 6, (position.y + 6).toInt(), 20, 20)
            WEAPON_EARTHQUAKE -> {
                g.fillOval(x + 10, (position.y + 10).toInt(), 3, 3)
                g.fillOval(x + 15, (position.y + 10).toInt(), 3, 3)
                g.fillOval(x + 20, (position.y + 10).toInt(), 3, 3)
                g.fillOval(x + 9, (position.y + 15).toInt(), 3, 3)
                g.fillOval(x + 14, (position.y + 15).toInt(), 3, 3)
                g.fillOval(x + 19, (position.y + 15).toInt(), 3, 3)
                g.fillOval(x + 10, (position.y + 20).toInt(), 3, 3)
                g.fillOval(x + 15, (position.y + 20).toInt(), 3, 3)
                g.fillOval(x + 20, (position.y + 20).toInt(), 3, 3)

            }
        }
    }

}