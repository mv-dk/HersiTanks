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
        // Undo transforms (from moving view by moving the mouse)
        val oldTranslationX = g.transform.translateX
        var oldTranslationY = g.transform.translateY
        g.translate(-oldTranslationX, -oldTranslationY)

        // Draw HUD
        g.color = Color.DARK_GRAY
        g.fillRect(position.x.toInt(), position.y.toInt(), parent.width, 32)

        g.stroke = stroke
        var i = 0
        var idx = 0
        var currentWeaponId = GameController.getCurrentPlayer().currentWeaponId

        for (weapon in Weapon.allWeapons.values) {
            if (weapon.id == currentWeaponId) {
                g.color = Color.RED
            } else {
                g.color = darkPurple
            }
            g.drawRect((position.x + i).toInt(), position.y.toInt(), 32, 32)
            if ((GameController.getCurrentPlayer().weaponry[weapon.id] ?: 0) > 0) {
                weapon.drawIcon(g, (position.x + i).toInt(), position.y.toInt())
            }
            i += 35
            idx += 1
        }
        val selectedWeapon = Weapon.allWeapons[currentWeaponId]
        val selectedWeaponName = selectedWeapon?.name
        val selectedWeaponAmmo = GameController.getCurrentPlayer().weaponry[selectedWeapon?.id]
        g.drawString("$selectedWeaponName ($selectedWeaponAmmo)", (position.x + i).toInt(), 42)

        // Redo transforms (moving view by moving the mouse)
        g.translate(oldTranslationX, oldTranslationY)
    }
}