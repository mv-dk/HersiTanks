package Experimental.TerrainScene

import Engine.*
import Experimental.Status.StatusLine
import Experimental.Status.StatusScreen
import Game.BattleState
import Game.GameController
import gameWindow
import menuGameScene
import java.awt.Color
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import kotlin.random.Random

val random = Random(1)

class TerrainGameScene(private val parentScene: IGameScene, color: Color, width: Int, height: Int) : GameScene(color, width, height) {
    lateinit var rasterTerrain: RasterTerrain
    var updatePlayersTurnOnNextPossibleOccasion = false
    var tankInfoBar = TankInfoBar(this, Pos2D(0.0, 0.0))
    var weaponBar = WeaponBar(this, Pos2D(0.0, 32.0))

    override fun load() {
        GameController.state = BattleState()
        rasterTerrain = RasterTerrain(this, Pos2D(0.0, 0.0))
        add(rasterTerrain)
        add(tankInfoBar)
        add(weaponBar)

        val margin = 40.0
        var numPlayers = GameController.players.size
        val spaceBetweenTanks = if (numPlayers == 1) (width-margin)/2.0 else ((width-2.0*margin) / (numPlayers-1))
        var x = margin
        for (i in 1 .. numPlayers){
            val p = GameController.players[i-1]
            val tank = Tank(this, rasterTerrain, Pos2D(x, 30.0), p.color)
            tank.falling = true
            p.tank = tank
            p.playing = true
            add(tank)
            x += spaceBetweenTanks
        }
    }

    fun busy(): Boolean{
        if (rasterTerrain.crumble || rasterTerrain.earthquake != null) return true
        if (GameController.players.any {it.playing && (it.tank?.falling == true)}) return true
        if (GameController.projectilesFlying > 0) return true
        if (GameController.explosionsActive > 0) return true
        return false
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_ESCAPE){
            GameController.onGoingToMenu()
            gameWindow?.gameRunner?.currentGameScene = menuGameScene
        }

        if (busy()) return

        if (e.keyCode == KeyEvent.VK_1) {
            rasterTerrain.mode = 1
        } else if (e.keyCode == KeyEvent.VK_2){
            rasterTerrain.mode = 2
        } else if (e.keyCode == KeyEvent.VK_3){
            rasterTerrain.mode = 3
        } else if (e.keyCode == KeyEvent.VK_LEFT) {
            AudioHelper.loop("change-angle", -1)
            GameController.getCurrentPlayersTank()?.increaseAngle(1)
        } else if (e.keyCode == KeyEvent.VK_RIGHT){
            AudioHelper.loop("change-angle", -1)
            GameController.getCurrentPlayersTank()?.increaseAngle(-1)
        } else if (e.keyCode == KeyEvent.VK_DOWN) {
            AudioHelper.loop("decrease-power", -1)
            GameController.getCurrentPlayersTank()?.increasePower(-1)
        } else if (e.keyCode == KeyEvent.VK_UP) {
            AudioHelper.loop("increase-power", -1)
            GameController.getCurrentPlayersTank()?.increasePower(1)
         } else if (e.keyCode == KeyEvent.VK_ENTER || e.keyCode == KeyEvent.VK_SPACE){
            AudioHelper.play("fire")
            val tank = GameController.getCurrentPlayersTank()
            if (tank != null) {
                val projectile = Projectile(
                    this, Pos2D(tank.canonX.toDouble(), tank.canonY.toDouble()),
                    Vec2D(
                        tank.position.copy(),
                        Pos2D(tank.canonX.toDouble(), tank.canonY.toDouble())
                    ).times(tank.power / 100.0)
                )
                add(projectile)
            }
            updatePlayersTurnOnNextPossibleOccasion = true
        } else if (e.keyCode == KeyEvent.VK_TAB) {
            val tank = GameController.getCurrentPlayersTank()
            if (tank != null) {
                tank.activeWeaponIdx = (tank.activeWeaponIdx + 1) % Weapon.allWeapons.size
                println("ActiveWeaponIdx: ${tank.activeWeaponIdx}")
            }
        } else if (e.keyCode == KeyEvent.VK_0) {
            val tank = GameController.getCurrentPlayersTank()
            if (tank != null) {
                tank.size += 1
                tank.updateCanonXY()
                println("Tank size: ${tank.size}")
            }
        } else if (e.keyCode == KeyEvent.VK_9){
            val tank = GameController.getCurrentPlayersTank()
            if (tank != null) {
                tank.size -= 1
                tank.updateCanonXY()
                println("Tank size: ${tank.size}")
            }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_LEFT || e.keyCode == KeyEvent.VK_RIGHT) {
            AudioHelper.stop("change-angle")
        } else if (e.keyCode == KeyEvent.VK_UP) {
            AudioHelper.stop("increase-power")
        } else if (e.keyCode == KeyEvent.VK_DOWN) {
            AudioHelper.stop("decrease-power")
        }
    }

    override fun mousePressed(e: MouseEvent) {
        if (!busy()) return
        if (e.button == MouseEvent.BUTTON1) {
            rasterTerrain.mouseClicked(e.x, e.y)
        } else if (e.button == MouseEvent.BUTTON3){
            AudioHelper.play("big-boom")
        }
    }

    override fun update() {
        if (!busy() && updatePlayersTurnOnNextPossibleOccasion) {
            val deadPlayer = GameController.players.firstOrNull{it.playing && it.tank?.energy == 0}
            val deadTank = deadPlayer?.tank
            if (deadPlayer != null && deadTank != null){
                remove(deadTank)
                deadTank.playing = false
                deadPlayer.playing = false
                add(Explosion(this, deadTank.position, 100, 40, { }))
            }
            if (!busy()) {
                updatePlayersTurnOnNextPossibleOccasion = false
                GameController.nextPlayersTurn()
                if ((GameController.state as BattleState).isBattleOver()) {
                    val team = GameController.getCurrentPlayersTeam()
                    team.victories += 1
                    GameController.gamesPlayed += 1
                    val statusLines = GameController.players.map {
                        StatusLine(it.name, it.victories(), it.money, it.color)
                    }

                    unload()
                    gameWindow?.gameRunner?.currentGameScene = StatusScreen(statusLines)
                }
            }
        }
        if (GameController.glowUp > 0) {
            GameController.glowUp -= 1
        }

        super.update()
    }
}

