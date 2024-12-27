package Experimental.TerrainScene

import Engine.*
import Experimental.Menu.*
import Experimental.Status.StatusLine
import Experimental.Status.StatusScreen
import Game.BattleState
import Game.GameController
import gameWindow
import menuGameScene
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import kotlin.random.Random

val random = Random(1)

class TerrainGameScene(private val parentScene: IGameScene, color: Color, width: Int, height: Int, val terrainWidth: Int) : GameScene(color, width, height) {
    lateinit var rasterTerrain: RasterTerrain
    var updatePlayersTurnOnNextPossibleOccasion = false
    var tankInfoBar = TankInfoBar(this, Pos2D(0.0, 0.0))
    var weaponBar = WeaponBar(this, Pos2D(0.0, 32.0))
    var skyImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    var translationX = 0
    var translationY = 0

    init {
        when (GameController.skyOption) {
            OPTION_SKY_BLUE -> {
                val g = skyImage.createGraphics()
                g.color = color
                g.fillRect(0, 0, width, height)
            }
            OPTION_SKY_STARRY -> {
                var g = skyImage.createGraphics()
                g.color = Color(0, 0, 50)
                g.fillRect(0, 0, width, height)
                g.color = Color(128,128,255)
                for (i in 1 .. 100) {
                    val size = random.nextInt(2,4)
                    g.fillArc(
                        random.nextInt(0, width),
                        random.nextInt(0, height),
                        size, size, 0, 360)
                }
            }
            OPTION_SKY_EVENING -> {
                var g = skyImage.createGraphics()
                var c = Color(255, 155, 0)
                g.color = c
                g.fillRect(0, 0, width, height)
                var bands = 60
                for (i in 1 .. bands) {
                    c = c.darker(200/bands)
                    g.color = c
                    g.fillRect(0, i*height/bands, width, height/bands)
                }
            }
        }

        when (GameController.groundOption) {
            OPTION_GROUND_SNOW -> {
                for (i in 1..100) {
                    add(Snowflake(this, Random.nextPos2D(width.toDouble(), height.toDouble())))
                }
            }
        }
    }

    override fun load() {
        GameController.state = BattleState()
        rasterTerrain = RasterTerrain(this, Pos2D(0.0, 0.0), terrainWidth, height)
        add(rasterTerrain)
        add(tankInfoBar)
        add(weaponBar)

        val margin = 40.0
        var numPlayers = GameController.players.size
        val spaceBetweenTanks = if (numPlayers == 1) (terrainWidth-margin)/2.0 else ((terrainWidth-2.0*margin) / (numPlayers-1))
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
        updateWind(true)
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
        if (updatePlayersTurnOnNextPossibleOccasion) return

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
        } else if (e.keyCode == KeyEvent.VK_COMMA) {
            translationX -= 1
        } else if (e.keyCode == KeyEvent.VK_PERIOD) {
            translationX += 1
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

    override fun mouseMoved(e: MouseEvent) {
        super.mouseMoved(e)
        translationY = Math.max(0, 300 - e.y)
        if (GameController.groundSizeOption == OPTION_GROUNDSIZE_SMALL) return
        if (GameController.groundSizeOption == OPTION_GROUNDSIZE_MEDIUM) {
            translationX = 500 - e.x
        } else if (GameController.groundSizeOption == OPTION_GROUNDSIZE_LARGE) {
            translationX = 500 - (e.x*2).toInt()
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
                } else {
                    updateWind(false)
                }
            }
        }
        if (GameController.glowUp > 0) {
            GameController.glowUp -= 1
        }

        if (GameController.groundOption == OPTION_GROUND_SNOW) {
            val pos = when {
                GameController.wind < 0 -> Pos2D(random.nextDouble(0.0, width + Math.abs(GameController.wind*height)), -1.0)
                GameController.wind > 0 -> Pos2D(random.nextDouble(-GameController.wind*height, width.toDouble()), -1.0)
                else -> Pos2D(random.nextDouble(0.0, width.toDouble()), -1.0)
            }
            add(Snowflake(this, pos))
        }

        super.update()
    }

    fun updateWind(first: Boolean) {
        if (first) {
            GameController.wind = when (GameController.windOption) {
                OPTION_WIND_NONE -> 0.0
                OPTION_WIND_LIGHT -> -1.0 + random.nextDouble() * 2.0
                OPTION_WIND_MEDIUM -> -2.0 + random.nextDouble() * 4.0
                OPTION_WIND_STRONG -> -8.0 + random.nextDouble() * 16.0
                else -> 0.0
            }
        } else {
            GameController.wind = when (GameController.windOption) {
                OPTION_WIND_NONE -> 0.0
                else -> GameController.wind + random.nextDouble(-Math.abs(GameController.wind * 0.05), Math.abs(GameController.wind * 0.05))
            }
        }
    }

    override fun draw(g: Graphics2D) {
        g.drawImage(skyImage, null, 0, 0)
        g.translate(translationX, translationY)
        gameObjectsByDrawOrder.forEach { it.draw(g) }
    }
}

