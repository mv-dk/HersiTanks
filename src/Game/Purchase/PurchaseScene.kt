package Game.Purchase

import Engine.*
import Game.Menu.FloatingBlob
import Game.Menu.MenuGameObject
import Game.Menu.MenuPoints.MenuPointGameObject
import Game.Menu.MenuPoints.ChangeSceneMenuPoint
import Game.Menu.Transition
import Game.TerrainScene.BattleScene
import Game.TerrainScene.Weapon
import Game.GameController
import Game.TerrainScene.Player.Player
import Game.TerrainScene.Player.PlayerType
import SND_BUY
import SND_BUY_FINISH
import gameResX
import gameResY
import gameWindow
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import kotlin.random.Random

class PurchaseScene(val players: List<Player>, val idx: Int) : GameScene(players[idx].color.contrast(0.3), gameResX, gameResY) {
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
        if (player.money >= 10) {
            menuPoints.add(
                MenuPointGameObject("Fuel, 10 L for $10",
                    this,
                    shadow = true,
                    cursor = false,
                    fontSize = 16,
                    onActivate = {
                        if (player.money >= 10) {
                            AudioHelper.play(SND_BUY)
                            player.fuel += 10
                            player.money -= 10
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
            BattleScene(GameController.groundSize)
        } else {
            PurchaseScene(players, idx + 1)
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
                    DelayedAction(this, i) {
                        menuGameObject.selectNext()
                    }
                    i += .1
                }
                DelayedAction(this, i) {
                    menuGameObject.activate()
                }
            }
            DelayedAction(this, i+0.5, {
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