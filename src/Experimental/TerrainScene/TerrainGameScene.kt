package Experimental.TerrainScene

import Engine.*
import Game.BattleState
import Game.GameController
import Game.Team
import gameWindow
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
        var numPlayers = 2
        try {
            numPlayers = GameController.settings["Players"] as Int
        } catch (e: Exception) { }
        val colors = listOf(Color.RED, Color.BLUE, Color.CYAN, Color.YELLOW, Color.BLACK, Color.WHITE, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.LIGHT_GRAY)
        val spaceBetweenTanks = if (numPlayers == 1) (width-margin)/2.0 else ((width-2.0*margin) / (numPlayers-1))
        var x = margin
        for (i in 1 .. numPlayers){
            val tank = Tank(this, rasterTerrain, Pos2D(x, 30.0), colors[i-1])
            tank.falling = true
            GameController.addTeam(Team("Tank$i", mutableListOf(tank)))
            add(tank)
            x += spaceBetweenTanks
        }
    }

    fun acceptInput(): Boolean{
        if (rasterTerrain.crumble || rasterTerrain.earthquake != null) return false
        if (GameController.tanks.any {it.alive && it.falling}) return false
        if (GameController.projectilesFlying > 0) return false
        if (GameController.explosionsActive > 0) return false
        return true
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_ESCAPE){
            GameController.onGoingToMenu()
            gameWindow?.gameRunner?.currentGameScene = parentScene
        }

        if (!acceptInput()) return

        if (e.keyCode == KeyEvent.VK_1) {
            rasterTerrain.mode = 1
        } else if (e.keyCode == KeyEvent.VK_2){
            rasterTerrain.mode = 2
        } else if (e.keyCode == KeyEvent.VK_3){
            rasterTerrain.mode = 3
        } else if (e.keyCode == KeyEvent.VK_LEFT) {
            AudioHelper.loop("change-angle", -1)
            GameController.getCurrentTank().increaseAngle(1)
        } else if (e.keyCode == KeyEvent.VK_RIGHT){
            AudioHelper.loop("change-angle", -1)
            GameController.getCurrentTank().increaseAngle(-1)
        } else if (e.keyCode == KeyEvent.VK_DOWN) {
            AudioHelper.loop("decrease-power", -1)
            GameController.getCurrentTank().increasePower(-1)
        } else if (e.keyCode == KeyEvent.VK_UP) {
            AudioHelper.loop("increase-power", -1)
            GameController.getCurrentTank().increasePower(1)
        } else if (e.keyCode == KeyEvent.VK_ENTER){
            AudioHelper.play("fire")
            val tank = GameController.getCurrentTank()
            val projectile = Projectile(this, tank.position.copy(),
                Vec2D(tank.position.copy(), Pos2D(tank.canonX.toDouble(), tank.canonY.toDouble())).times(tank.power/100.0))
            add(projectile)
            updatePlayersTurnOnNextPossibleOccasion = true
        } else if (e.keyCode == KeyEvent.VK_TAB) {
            val tank = GameController.getCurrentTank()
            tank.activeWeapon = (tank.activeWeapon + 1) % 10
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
        if (!acceptInput()) return
        if (e.button == MouseEvent.BUTTON1) {
            rasterTerrain.mouseClicked(e.x, e.y)
        } else if (e.button == MouseEvent.BUTTON3){
            AudioHelper.play("big-boom")
        }
    }

    override fun update() {
        if (updatePlayersTurnOnNextPossibleOccasion && acceptInput()){
            updatePlayersTurnOnNextPossibleOccasion = false
            GameController.nextPlayersTurn()
        }
        super.update()
    }
}

