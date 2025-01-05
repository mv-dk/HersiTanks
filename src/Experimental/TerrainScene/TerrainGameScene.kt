package Experimental.TerrainScene

import Engine.*
import Experimental.Menu.*
import Experimental.Status.StatusLine
import Experimental.Status.StatusScreen
import Game.BattleState
import Game.GameController
import SND_CHANGE_ANGLE
import SND_DECREASE_POWER
import SND_FIRE
import SND_FIRE2
import SND_FIRE3
import SND_FIZZLE
import SND_INCREASE_POWER
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
    var keyPressed : Int? = null
    var viewport = Viewport(
        this,
        width,
        height,
        0,
        0,
        minY = -400,
        maxY = 0,
        minX = if (GameController.groundSizeOption == OPTION_GROUNDSIZE_SMALL) 0 else Int.MIN_VALUE,
        maxX = if (GameController.groundSizeOption == OPTION_GROUNDSIZE_SMALL) 0 else Int.MAX_VALUE
    )
    var mouseWasMoved = false

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
                add(SnowMaker(this, Pos2D(0.0, 0.0), terrainWidth))
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
        val numPlayers = GameController.players.size
        val spaceBetweenTanks = if (numPlayers == 1) (terrainWidth-margin)/2.0 else ((terrainWidth-2.0*margin) / (numPlayers-1))
        var x = margin
        val randomIndices = (0..<numPlayers).shuffled()
        repeat (numPlayers){
            val p = GameController.players[randomIndices[it]]
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

    /**
     * This is running on every update.
     */
    fun handleKeyPressed() {
        if (keyPressed == KeyEvent.VK_ESCAPE){
            keyPressed = null
            GameController.onGoingToMenu()
            gameWindow?.gameRunner?.currentGameScene = menuGameScene
        }

        if (busy()) return
        if (updatePlayersTurnOnNextPossibleOccasion) return

        when (keyPressed) {
            KeyEvent.VK_LEFT -> {
                AudioHelper.loop(SND_CHANGE_ANGLE, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increaseAngle(1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                }
            }
            KeyEvent.VK_RIGHT -> {
                AudioHelper.loop(SND_CHANGE_ANGLE, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increaseAngle(-1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                }
            }
            KeyEvent.VK_DOWN -> {
                AudioHelper.loop(SND_DECREASE_POWER, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increasePower(-1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                }
            }
            KeyEvent.VK_UP -> {
                AudioHelper.loop(SND_INCREASE_POWER, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increasePower(1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                }
            }
            KeyEvent.VK_PAGE_DOWN -> {
                AudioHelper.loop(SND_DECREASE_POWER, -1)
                GameController.getCurrentPlayersTank()?.increasePower(-10)
            }
            KeyEvent.VK_PAGE_UP -> {
                AudioHelper.loop(SND_INCREASE_POWER, -1)
                GameController.getCurrentPlayersTank()?.increasePower(10)
            }
            KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                keyPressed = null
                when (Random.nextInt(3)) {
                    0 -> AudioHelper.play(SND_FIRE)
                    1 -> AudioHelper.play(SND_FIRE2)
                    2 -> AudioHelper.play(SND_FIRE3)
                }

                val player = GameController.getCurrentPlayer()
                val tank = player.tank
                if ((player.weaponry[player.currentWeaponId] ?: 0) == 0) {
                    AudioHelper.play(SND_FIZZLE);
                } else {
                    if (tank != null) {
                        val velocity = Vec2D(
                            tank.position.copy(),
                            Pos2D(tank.canonX.toDouble(), tank.canonY.toDouble())).times(tank.power / 400.0)
                        val position = Pos2D(tank.canonX.toDouble(), tank.canonY.toDouble())
                        val projectile = Weapon.allWeapons[player.currentWeaponId]?.getProjectile(this, position, velocity)
                        if (projectile != null) {
                            add(projectile)
                        }
                        player.decreaseAmmoAndCycleIfZero()
                    }
                }
                updatePlayersTurnOnNextPossibleOccasion = true
            }
            KeyEvent.VK_TAB -> {
                keyPressed = null
                GameController.getCurrentPlayer().cycleWeapon()
            }
            KeyEvent.VK_0 -> {
                val tank = GameController.getCurrentPlayersTank()
                if (tank != null) {
                    tank.size += 1
                    tank.updateCanonXY()
                    println("Tank size: ${tank.size}")
                }
            }
            KeyEvent.VK_9 -> {
                val tank = GameController.getCurrentPlayersTank()
                if (tank != null) {
                    tank.size -= 1
                    tank.updateCanonXY()
                    println("Tank size: ${tank.size}")
                }
            }
        }
    }

    override fun keyPressed(e: KeyEvent) {
        keyPressed = e.keyCode
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_LEFT || e.keyCode == KeyEvent.VK_RIGHT) {
            AudioHelper.stop(SND_CHANGE_ANGLE)
        } else if (e.keyCode == KeyEvent.VK_UP || e.keyCode == KeyEvent.VK_PAGE_UP) {
            AudioHelper.stop(SND_INCREASE_POWER)
        } else if (e.keyCode == KeyEvent.VK_DOWN || e.keyCode == KeyEvent.VK_PAGE_DOWN) {
            AudioHelper.stop(SND_DECREASE_POWER)
        }
        keyPressed = null
    }

    override fun mouseMoved(e: MouseEvent) {
        super.mouseMoved(e)
        mouseWasMoved = true
        translationY = Math.min(180, e.y - 300)
        if (GameController.groundSizeOption == OPTION_GROUNDSIZE_SMALL) return
        if (GameController.groundSizeOption == OPTION_GROUNDSIZE_MEDIUM) {
            translationX = e.x
        } else if (GameController.groundSizeOption == OPTION_GROUNDSIZE_LARGE) {
            translationX = e.x * 2
        }
    }

    override fun update() {
        handleKeyPressed()
        if (!busy() && updatePlayersTurnOnNextPossibleOccasion) {
            val deadPlayer = GameController.players.firstOrNull{it.playing && it.tank?.energy == 0}
            val deadTank = deadPlayer?.tank
            if (deadPlayer != null && deadTank != null){
                remove(deadTank)
                deadTank.playing = false
                deadPlayer.playing = false
                viewport.setFocus(deadTank.position)
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
                    GameController.getCurrentPlayersTank()?.let { viewport.setFocus(it.position) }
                }
            }
        }
        if (mouseWasMoved) {
            mouseWasMoved = false
            viewport.setFocus(translationX.toDouble(), translationY.toDouble())
        }
        else if (Projectile.activeProjectiles.size > 0) {
            val p = Projectile.activeProjectiles.first()
            viewport.setFocus(p.position)
        }
        else if (GameController.players.any { it.playing && it.tank?.falling == true }) {
            val p = GameController.players.first { it.playing && it.tank?.falling == true }.tank?.position
            if (p != null)
                viewport.setFocus(p)
        }
        viewport.update()
        if (GameController.glowUp > 0) {
            GameController.glowUp -= 1
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
        //g.translate(translationX, translationY)
        g.translate(-viewport.x, -viewport.y)
        gameObjectsByDrawOrder.forEach {
            if (it is RasterTerrain) {
                it.draw(g)
            } else if (it is WeaponBar) {
                it.draw(g)
            } else if (it is TankInfoBar) {
                it.draw(g)
            } else if (it is GameObject2) {
                if (viewport.inside(it)) {
                    it.draw(g)
                }
            }
        }
    }
}

