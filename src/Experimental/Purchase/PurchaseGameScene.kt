package Experimental.Purchase

import Engine.*
import Experimental.Menu.FloatingBlob
import Experimental.Menu.MenuGameObject
import Experimental.Menu.MenuPoints.MenuPointGameObject
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.Menu.Transition
import Experimental.TerrainScene.TerrainGameScene
import Experimental.TerrainScene.Weapon
import Game.GameController
import Game.Player
import Game.PlayerType
import SND_BUY
import SND_BUY_FINISH
import gameResX
import gameResY
import gameWindow
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import kotlin.random.Random

class PurchaseGameScene(val players: List<Player>, val idx: Int) : GameScene(players[idx].color.contrast(0.3), gameResX, gameResY) {
    val menuPoints = mutableListOf<MenuPointGameObject>()
    val player = players[idx]

    init {
        repeat(10) {
            add(FloatingBlob(this))
        }

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
        leftMargin = 20.0,
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

        if (player.playerType == PlayerType.LocalCpu) {
            var i = 1.0
            repeat(Random.nextInt(1,3)) {
                repeat(Random.nextInt(menuPoints.size)) {
                    DelayedAction(i) {
                        menuGameObject.selectNext()
                    }
                    i += .1
                }
                DelayedAction(i) {
                    menuGameObject.activate()
                }
            }
            DelayedAction(i+0.5, {
                menuGameObject.onEscapePressed()
            })
        }
    }

    private var keyHasBeenReleasedOnce = false
    override fun keyReleased(e: KeyEvent) {
        keyHasBeenReleasedOnce = true
    }

    override fun keyPressed(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        if (player.playerType != PlayerType.LocalHuman) return

        menuGameObject.keyPressed(e)
    }

    override fun draw(g: Graphics2D) {
        super.draw(g)
        g.color = players[idx].color
        g.drawString("${players[idx].name} (\$${players[idx].money})", 10, 30)
    }
}