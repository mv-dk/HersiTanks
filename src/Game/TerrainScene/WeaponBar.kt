package Game.TerrainScene

import Engine.*
import Game.GameController
import Game.Menu.OPTION_SKY_BLUE
import Game.Menu.OPTION_SKY_EVENING
import Game.Menu.OPTION_SKY_STARRY
import Game.TerrainScene.Player.Player
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

class WeaponBar(parent: IGameScene, position: Pos2D) : GameObject2(parent, position) {
    private val thinStroke = BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    private val thickStroke = BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    private val nightBorderColor = Color(128,128,255)
    private val dayBorderColor = Color(192, 192, 255)
    private val eveningBorderColor = Color(110, 8, 0)
    private val parentAsBattleScene = parent as? BattleScene
    private val skyType
        get() = parentAsBattleScene?.skyType ?: OPTION_SKY_BLUE

    init {
        drawOrder = 100
    }

    override fun update() {

    }

    override fun draw(g: Graphics2D) {
        drawAsHud(g) {
            // Draw HUD
            g.stroke = thinStroke
            var i = 8

            val currentPlayer = GameController.getCurrentPlayer() ?: return@drawAsHud
            val currentWeaponId = currentPlayer.currentWeaponId

            for (weapon in Weapon.allWeapons.values) {
                val hasThisWeapon = (currentPlayer.weaponry[weapon.id] ?: 0) > 0
                if (!hasThisWeapon) continue

                val isSelected = currentPlayer.currentWeaponId == weapon.id

                drawWeaponBorder(isSelected, g, currentPlayer, i)
                drawWeaponIcon(g, weapon, i)

                i += 35
            }
            i += 3
            val selectedWeapon = Weapon.allWeapons[currentWeaponId]
            val selectedWeaponName = selectedWeapon?.name
            val selectedWeaponAmmo = currentPlayer.weaponry[selectedWeapon?.id] ?: 0
            g.drawString("$selectedWeaponName ($selectedWeaponAmmo)", (position.x + i).toInt(), 50)
        }
    }

    private fun getBorderColor() :Color {
        return when (skyType) {
            OPTION_SKY_BLUE -> dayBorderColor
            OPTION_SKY_EVENING -> eveningBorderColor
            OPTION_SKY_STARRY -> nightBorderColor
            else -> Color.BLACK
        }
    }

    private fun drawWeaponIcon(
        g: Graphics2D,
        weapon: Weapon,
        i: Int
    ) {
        g.color = getBorderColor()
        g.stroke = thickStroke
        weapon.drawIcon(g, (position.x + i).toInt(), position.y.toInt())
    }

    private fun drawWeaponBorder(
        isSelected: Boolean,
        g: Graphics2D,
        currentPlayer: Player,
        i: Int
    ) {
        if (isSelected) {
            g.stroke = thickStroke
            g.color = currentPlayer.color
        } else {
            g.stroke = thinStroke
            g.color = getBorderColor()
        }
        g.drawRect((position.x + i).toInt(), position.y.toInt(), 30, 30)
    }
}