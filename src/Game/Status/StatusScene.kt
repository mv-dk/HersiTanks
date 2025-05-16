package Game.Status

import Engine.GameScene
import Game.Menu.MenuScene
import Game.Purchase.PurchaseScene
import Game.TerrainScene.BattleScene
import Game.TerrainScene.Weapon
import Game.GameController
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class StatusScene(val lines: List<StatusLine>) : GameScene(Color(182, 179, 173), gameResX, gameResY) {
    val headerFont = Font("Helvetica", Font.BOLD, 22)

    override fun load() {
        println("Loaded status screen")
    }

    override fun keyPressed(e: KeyEvent) {
        super.keyPressed(e)
        if (GameController.gamesPlayed == GameController.gamesToPlay) {
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuScene()
        } else {

            val cheapestWeaponPrice = Weapon.allWeapons.minOf { it.value.purchasePrice }
            val playersAbleToPurchase = GameController.players.filter { it.money > cheapestWeaponPrice }
            if (playersAbleToPurchase.size > 0) {
                gameWindow?.gameRunner?.currentGameScene =
                    PurchaseScene(GameController.players.filter { it.money > cheapestWeaponPrice }, 0)
            } else {
                gameWindow?.gameRunner?.currentGameScene = BattleScene(GameController.groundSize)
            }
        }
    }

    override fun draw(g: Graphics2D) {
        super.draw(g)
        g.color = Color.BLACK
        g.font = headerFont
        g.drawString("Round ${GameController.gamesPlayed} of ${GameController.gamesToPlay}", 250, 40)

        var y = 60
        for (l in lines) {
            l.draw(g, 100, y)
            y += 20
        }
    }
}

class StatusLine(name: String, wins: Int, money: Double, val color: Color) {
    val statusLineFont = Font("Helvetica", Font.BOLD, 18)
    val text = "$name: $wins (\$$money)"

    fun draw(g: Graphics2D, x: Int, y: Int){
        g.color = color
        g.font = statusLineFont
        g.drawString(text, x, y)
    }
}