package Experimental.Purchase

import Engine.AudioHelper
import Engine.GameScene
import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.FloatingBlob
import Experimental.Menu.MenuGameObject
import Experimental.Menu.MenuPoints.MenuPointGameObject
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.Menu.Transition
import Experimental.TerrainScene.TerrainGameScene
import Experimental.TerrainScene.Weapon
import Game.GameController
import Game.Player
import SND_BUY
import SND_BUY_FINISH
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class PurchaseGameScene(val players: List<Player>, val idx: Int) : GameScene(Color(128,150, 132), gameResX, gameResY) {
    val menuPoints = mutableListOf<MenuPointGameObject>()

    init {
        repeat(10) {
            add(FloatingBlob(this))
        }

        val player = players[idx]
        for (w in Weapon.allWeapons.filter({ it.value.purchasePrice <= player.money}).map{it.value}) {
            menuPoints.add(
                MenuPointGameObject("${w.name}, ${w.purchaseQuantity} for \$${w.purchasePrice}",
                this,
                shadow = true,
                cursor = false,
                fontSize = 16,
                onActivate = {
                    if (player.money >= w.purchasePrice) {
                        AudioHelper.play(SND_BUY)
                        if (player.weaponry.containsKey(w.id)) {
                            player.weaponry[w.id] = (player.weaponry[w.id] ?: 0) + w.purchaseQuantity
                        } else {
                            player.weaponry.put(w.id, w.purchaseQuantity)
                        }
                        player.money -= w.purchasePrice
                    }
            })
            )
        }
        if (idx == players.size - 1) {
            menuPoints.add(
                ChangeSceneMenuPoint("Done", this, ::nextScene)
            )
        } else {
            menuPoints.add(
                ChangeSceneMenuPoint("Done", this, ::nextScene)
            )
        }

        add(Transition(this))
    }

    private fun nextScene(): IGameScene {
        AudioHelper.play(SND_BUY_FINISH)
        return if (idx == players.size - 1) {
            TerrainGameScene(GameController.groundSize)
        } else {
            PurchaseGameScene(players, idx + 1)
        }
    }

    private val menuGameObject = MenuGameObject(
        this,
        Pos2D(100.0, 60.0),
        300,
        400,
        25.0,
        menuPoints,
        onEscapePressed = {
            unload()
            gameWindow?.gameRunner?.currentGameScene = nextScene()
        }
    )
    override fun update() {
        super.update()
    }

    override fun load() {
        add(menuGameObject)
    }

    private var keyHasBeenReleasedOnce = false
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