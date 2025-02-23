package Experimental.TerrainScene

import Engine.*
import Experimental.Menu.*
import Experimental.Status.StatusLine
import Experimental.Status.StatusScreen
import Game.*
import SND_CHANGE_ANGLE
import SND_DECREASE_POWER
import SND_INCREASE_POWER
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import kotlin.random.Random

val random = Random(1)

class TerrainGameScene(val terrainWidth: Int) : GameScene(Color(113, 136, 248), gameResX, gameResY) {
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
    var decision: PlayerDecision? = null

    init {
        when (GameController.skyOption) {
            OPTION_SKY_BLUE -> {
                val g = skyImage.createGraphics()
                var c = Color(12, 138, 255)
                g.color = c
                g.fillRect(0, 0, width, height)
                var bands = 60
                for (i in 1 .. bands) {
                    c = c.lighter(150/bands)
                    g.color = c
                    g.fillRect(0, i*height/bands, width, height/bands)
                }
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

        add(Transition(this))
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

        repeat(Random.nextInt(3)) {
            CloudMaker.make(
                this,
                position = Pos2D(
                    Random.nextInt(0, terrainWidth),
                    Random.nextInt(0, 100)
                ),
                width = Random.nextInt(200, 400))
        }

        repeat(Random.nextInt(numPlayers)) {
            GameController.nextPlayersTurn()
        }
        updatePlayersTurnOnNextPossibleOccasion = true
    }

    fun busy(): Boolean{
        if (rasterTerrain.crumble || rasterTerrain.earthquake != null) return true
        if (GameController.players.any {it.playing && (it.tank?.falling == true)}) return true
        if (GameController.projectilesFlying > 0) return true
        if (GameController.explosionsActive > 0) return true
        if (decision != null) return true
        return false
    }

    fun showDecisionOutcome() {
        GameController.getCurrentPlayer().also { player ->
            player.tank?.also { tank ->
                val p = PlayerDecision(
                    player,
                    tank.angle.toInt() ?: 0,
                    tank.power ?: 0,
                    1
                ).getSimulatedExplosionLocation(tank)
                add(ProjectileTrail(this, p.copy(), Color.BLACK))
            }
        }
    }

    /**
     * This is running on every update.
     */
    fun handleKeyPressed() {
        if (keyPressed == KeyEvent.VK_ESCAPE){
            keyPressed = null
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuGameScene()
            GameController.onGoingToMenu()
            return
        }

        if (busy()) return
        if (updatePlayersTurnOnNextPossibleOccasion) return
        if (GameController.getCurrentPlayer().playerType != PlayerType.LocalHuman) return

        when (keyPressed) {
            KeyEvent.VK_LEFT -> {
                AudioHelper.loop(SND_CHANGE_ANGLE, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increaseAngle(1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                    //showDecisionOutcome()
                }
            }
            KeyEvent.VK_RIGHT -> {
                AudioHelper.loop(SND_CHANGE_ANGLE, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increaseAngle(-1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                    //showDecisionOutcome()
                }
            }
            KeyEvent.VK_DOWN -> {
                AudioHelper.loop(SND_DECREASE_POWER, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increasePower(-1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                    //showDecisionOutcome()
                }
            }
            KeyEvent.VK_UP -> {
                AudioHelper.loop(SND_INCREASE_POWER, -1)
                GameController.getCurrentPlayersTank()?.let {
                    it.increasePower(1)
                    if (!viewport.inside(it)) {
                        viewport.setFocus(it.position)
                    }
                    //showDecisionOutcome()
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
                GameController.getCurrentPlayer().fire()
                updatePlayersTurnOnNextPossibleOccasion = true
            }
            KeyEvent.VK_TAB -> {
                keyPressed = null
                GameController.getCurrentPlayer().cycleWeapon()
            }
            KeyEvent.VK_E -> { // Toy
                GameController.getCurrentPlayersTank()?.addFire()
            }
            KeyEvent.VK_U -> { // Toy
                GameController.players.forEach {
                    it.tank?.fireEmitter?.let { emitter ->
                        emitter.emitTicksLeft = 0
                    }
                    it.tank?.fireEmitter = null
                    it.tank?.smokeEmitter?.let { emitter ->
                        emitter.emitTicksLeft = 0
                    }
                    it.tank?.smokeEmitter = null
                }
            }
            KeyEvent.VK_O -> { // Toy
                add(Transition(this))
            }
            KeyEvent.VK_0 -> { // Toy
                val tank = GameController.getCurrentPlayersTank()
                if (tank != null) {
                    tank.size += 1
                    tank.updateCanonXY()
                    println("Tank size: ${tank.size}")
                }
            }
            KeyEvent.VK_1 -> { // Toy
                rasterTerrain.addColoredTopLayer(rasterTerrain.rasterImage, 2, Color.BLACK)
            }
            KeyEvent.VK_2 -> { // Toy
                CloudMaker.make(
                    this,
                    Pos2D(
                        Random.nextInt(0, terrainWidth),
                        Random.nextInt(0, 100)),
                    Random.nextInt(200, 400))
            }
            KeyEvent.VK_9 -> { // Toy
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
            handleNextPlayersTurn()
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
            GameController.players.first { it.playing && it.tank?.falling == true }.tank?.position?.let {
                viewport.setFocus(it)
            }
        }
        viewport.update()
        if (GameController.glowUp > 0) {
            GameController.glowUp -= 1
        }

        if (GameController.getCurrentPlayer().playerType == PlayerType.LocalCpu) {
            carryOutDecision()
        }

        super.update()
    }

    private fun carryOutDecision() {
        decision?.let {
            val player = GameController.getCurrentPlayer()
            val tank = player.tank ?: return
            if (tank.angle.toInt() < it.angle) {
                tank.increaseAngle(+1)
                AudioHelper.loop(SND_CHANGE_ANGLE)
            } else if (tank.angle.toInt() > it.angle) {
                tank.increaseAngle(-1)
                AudioHelper.loop(SND_CHANGE_ANGLE)
            } else if (tank.power != it.power) {
                AudioHelper.stop(SND_CHANGE_ANGLE)
                if (tank.power > it.power) {
                    AudioHelper.loop(SND_DECREASE_POWER)
                    tank.increasePower(-1)
                } else {
                    AudioHelper.loop(SND_INCREASE_POWER)
                    tank.increasePower(1)
                }
            } else {
                AudioHelper.stop(SND_CHANGE_ANGLE)
                AudioHelper.stop(SND_DECREASE_POWER)
                AudioHelper.stop(SND_INCREASE_POWER)
                player.fire()
                updatePlayersTurnOnNextPossibleOccasion = true
                decision = null
            }
        }
    }

    private fun handleNextPlayersTurn() {
        explodeDeadPlayers()
        if (!busy()) {
            updatePlayersTurnOnNextPossibleOccasion = false
            GameController.nextPlayersTurn()
            if ((GameController.state as BattleState).isBattleOver()) {
                handleBattleOver()
            } else {
                updateWind(false)
                GameController.getCurrentPlayersTank()?.let { viewport.setFocus(it.position) }
            }

            val currentPlayer = GameController.getCurrentPlayer()
            if (currentPlayer.playerType == PlayerType.LocalCpu) {
                if (decision == null) {
                    DelayedAction(this, 1.0, {
                        decision = MonteCarloCpu(repetitions = 10, showDecisionOutcomes = false).getDecision(currentPlayer)
                    })
                }
            }
        }
    }

    private fun handleBattleOver() {
        val team = GameController.getCurrentPlayersTeam()
        team.players.filter { it.playing && (it.tank?.energy ?: 0) > 0 }.let {survivors ->
            for (player in survivors) {
                player.money += 100
            }
        }
        team.victories += 1
        GameController.gamesPlayed += 1
        val statusLines = GameController.players.map {
            StatusLine(it.name, it.victories(), it.money, it.color)
        }

        unload()
        gameWindow?.gameRunner?.currentGameScene = StatusScreen(statusLines)
    }

    private fun explodeDeadPlayers() {
        val deadPlayer = GameController.players.firstOrNull { it.playing && it.tank?.energy == 0 }
        val deadTank = deadPlayer?.tank
        if (deadPlayer != null && deadTank != null) {
            deadTank.onDie()
            remove(deadTank)
            deadTank.playing = false
            deadPlayer.playing = false
            viewport.setFocus(deadTank.position)
            add(Explosion(this, deadTank.position, 100, 40, { }))
        }
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

