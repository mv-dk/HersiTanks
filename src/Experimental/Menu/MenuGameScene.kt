package Experimental.Menu

import Engine.DelayedAction
import Engine.GameRunner
import Engine.GameScene
import Engine.Pos2D
import Experimental.EditPlayers.EditPlayers
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.Menu.MenuPoints.ExitGameMenuPoint
import Experimental.Menu.MenuPoints.NumberSelectorMenuPoint
import Experimental.Menu.MenuPoints.ToggleFullScreenMenuPoint
import Experimental.particles.Emitter
import Experimental.particles.FireEmitter
import Experimental.particles.SmokeEmitter
import Game.GameController
import Game.Player
import Game.Team
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints.*
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

val OPTION_GROUND_GRASS = 0
val OPTION_GROUND_SNOW = 1

val OPTION_SKY_BLUE = 0
val OPTION_SKY_STARRY = 1
val OPTION_SKY_EVENING = 2

val OPTION_WIND_NONE = 0
val OPTION_WIND_LIGHT = 1
val OPTION_WIND_MEDIUM = 2
val OPTION_WIND_STRONG = 3

val OPTION_DECO_NONE = 0
val OPTION_DECO_CHRISTMAS = 1

val OPTION_GROUNDSIZE_SMALL = 0
val OPTION_GROUNDSIZE_MEDIUM = 1
val OPTION_GROUNDSIZE_LARGE = 2

class MenuGameScene() : GameScene(Color(77, 83, 128), gameResX, gameResY) {

    companion object {
        var numPlayersSelected = 2
        var numGamesSelected = 10
    }

    val menuPoints = mutableListOf(
        ChangeSceneMenuPoint("Go!", this, {
            GameController.teams.clear()
            GameController.players.clear()
            GameController.gamesToPlay = numGamesSelected
            GameController.gamesPlayed = 0
            val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.BLACK, Color.WHITE, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.LIGHT_GRAY)
            for (i in 1 .. numPlayersSelected) {
                val newPlayer = Player("Player $i")
                newPlayer.weaponry.put(1, 200)
                newPlayer.weaponry.put(2, 100)
//                newPlayer.weaponry.put(3, 100)
//                newPlayer.weaponry.put(4, 100)
//                newPlayer.weaponry.put(5, 100)
//                newPlayer.weaponry.put(6, 100)
//                newPlayer.weaponry.put(7, 100)
//                newPlayer.weaponry.put(8, 100)
//                newPlayer.weaponry.put(9, 100)

                newPlayer.color = colors[i-1]
                GameController.teams.add(Team("Team $i", listOf(newPlayer)))
                GameController.players.add(newPlayer)
            }
            EditPlayers()
        }),
        NumberSelectorMenuPoint("Players", this, numPlayersSelected, 2, 10, onChange = {_,new ->
            numPlayersSelected = new
        }),
        NumberSelectorMenuPoint("Rounds", this, numGamesSelected, 1, 99, onChange = {_,new ->
            numGamesSelected = new
        }),

        //MenuPointGameObject("Settings", parent, nextMenuPointPos()),
        ChangeSceneMenuPoint("Settings", this, { SettingsGameScene() }),
        ChangeSceneMenuPoint("About", this, { AboutGameScene() }),
        ExitGameMenuPoint("Exit", this)
    )
    val menuGameObject = MenuGameObject(
        this,
        Pos2D(30.0, 100.0),
        200,
        300,
        30.0,
        menuPoints,
        onEscapePressed =
        {
            DelayedAction(0.5) {
                unload()
                GameRunner.exitGame = true
            }
        })

    init {
        repeat(10) {
            add(FloatingBlob(this))
        }
        add(menuGameObject)
        add(Transition(this))
    }

    override fun load() {
    }

    override fun keyTyped(e: KeyEvent) {
        menuGameObject.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        menuGameObject.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) = Unit


}

