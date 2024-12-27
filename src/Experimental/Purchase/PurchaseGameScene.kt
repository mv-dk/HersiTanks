package Experimental.Purchase

import Engine.GameScene
import Engine.Pos2D
import Experimental.Menu.MenuGameObject
import Experimental.Menu.MenuPointGameObject
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.TerrainScene.TerrainGameScene
import Experimental.TerrainScene.Weapon
import Game.GameController
import Game.Player
import gameResX
import gameResY
import menuGameScene
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class PurchaseGameScene(val players: List<Player>, val idx: Int) : GameScene(Color(128,150, 132), gameResX, gameResY) {
    val menuPoints = mutableListOf<MenuPointGameObject>()

    init {
        val player = players[idx]
        for (w in Weapon.allWeapons.filter({ it.value.purchasePrice <= player.money}).map{it.value}) {
            menuPoints.add(MenuPointGameObject("${w.name}, ${w.purchaseQuantity} for \$${w.purchasePrice}",
                this,
                shadow = true,
                cursor = false,
                fontSize = 16,
                onActivate = {
                    if (player.money >= w.purchasePrice) {
                        if (player.weaponry.containsKey(w.id)) {
                            player.weaponry[w.id] = (player.weaponry[w.id] ?: 0) + w.purchaseQuantity
                        } else {
                            player.weaponry.put(w.id, w.purchaseQuantity)
                        }
                        player.money -= w.purchasePrice
                    }
            }))
        }
        if (idx == players.size - 1) {
            menuPoints.add(ChangeSceneMenuPoint("Done", this, { TerrainGameScene(menuGameScene, Color(113, 136, 248), gameResX, gameResY, GameController.groundSize) }))
        } else {
            menuPoints.add(ChangeSceneMenuPoint("Done", this, { PurchaseGameScene(players, idx + 1)}))
        }

    }
    val menuGameObject = MenuGameObject(this, Pos2D(100.0, 60.0), 300, 400, 25.0, menuPoints)
    override fun update() {

    }

    override fun load() {
        add(menuGameObject)
    }

    var keyHasBeenReleasedOnce = false
    override fun keyReleased(e: KeyEvent) {
        keyHasBeenReleasedOnce = true
    }

    override fun keyPressed(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        menuGameObject.keyPressed(e)
    }

    override fun draw(g: Graphics2D) {
        super.draw(g)
        g.color = players[idx].color
        g.drawString("${players[idx].name} (\$${players[idx].money})", 10, 30)
    }
}