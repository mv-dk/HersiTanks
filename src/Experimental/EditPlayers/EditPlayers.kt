package Experimental.EditPlayers

import Engine.GameScene
import Engine.Pos2D
import Experimental.Menu.FloatingBlob
import Experimental.Menu.MenuGameObject
import Experimental.Menu.MenuGameScene
import Experimental.Menu.MenuPoints.MenuPointGameObject
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.Menu.MenuPoints.TextInputMenuPoint
import Experimental.Menu.Transition
import Experimental.TerrainScene.TerrainGameScene
import Game.GameController
import Game.Player
import Game.PlayerType
import Game.Team
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.event.KeyEvent

class EditPlayers() : GameScene(Color(123, 129, 78), gameResX, gameResY) {
    private val menuPoints = mutableListOf<MenuPointGameObject>()
    init {
        repeat(10) {
            add(FloatingBlob(this))
        }

        val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.BLACK, Color.WHITE, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.LIGHT_GRAY)

        for (p in 0..<GameController.numberOfPlayersOption) {
            val newMenuPoint = TextInputMenuPoint("Name", this, Pos2D(0.0, 0.0), "Player ${p+1}", colors[p], 18)
            newMenuPoint.unselectedColor = Color.BLACK
            newMenuPoint.selectedColor = colors[p]
            menuPoints.add(newMenuPoint)
        }
        menuPoints.add(ChangeSceneMenuPoint("Start!", this, {
            GameController.players.clear()
            GameController.teams.clear()
            menuPoints.forEachIndexed { idx, menuPoint ->
                if (menuPoint is TextInputMenuPoint) {
                    val playerType = if (menuPoint.textValue.startsWith("cpu.")) PlayerType.LocalCpu else PlayerType.LocalHuman
                    val newPlayer = Player(menuPoint.textValue, playerType)
                    newPlayer.weaponry.put(1, 200)
                    newPlayer.weaponry.put(2, 25)
//                newPlayer.weaponry.put(3, 100)
//                newPlayer.weaponry.put(4, 100)
//                newPlayer.weaponry.put(5, 100)
//                newPlayer.weaponry.put(6, 100)
//                newPlayer.weaponry.put(7, 100)
//                newPlayer.weaponry.put(8, 100)
//                newPlayer.weaponry.put(9, 100)

                    newPlayer.color = colors[idx]
                    GameController.teams.add(Team("Team $i", listOf(newPlayer)))
                    GameController.players.add(newPlayer)
                }
            }

            TerrainGameScene(GameController.groundSize)
        }))

        add(Transition(this))
    }

    private val menuGameObject = MenuGameObject(
        this,
        Pos2D(100.0, 20.0),
        300,
        400,
        25.0,
        menuPoints,
        onEscapePressed =
        {
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuGameScene()
        })
    init {
        add(menuGameObject)
    }

    override fun load() { }

    private var keyHasBeenReleasedOnce = false
    override fun keyTyped(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        menuGameObject.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        menuGameObject.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) {
        keyHasBeenReleasedOnce = true
    }

}