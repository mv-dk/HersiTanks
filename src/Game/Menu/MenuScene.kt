package Game.Menu

import Engine.DelayedAction
import Engine.GameRunner
import Engine.GameScene
import Engine.Pos2D
import Game.EditPlayers.EditPlayersScene
import Game.GameController
import Game.Menu.MenuPoints.ChangeSceneMenuPoint
import Game.Menu.MenuPoints.ExitGameMenuPoint
import Game.Menu.MenuPoints.NumberSelectorMenuPoint
import gameResX
import gameResY
import java.awt.Color
import java.awt.event.KeyEvent

val OPTION_GROUND_RANDOM = 0
val OPTION_GROUND_GRASS = 1
val OPTION_GROUND_SNOW = 2
val OPTION_GROUND_DESERT = 3

val OPTION_SKY_BLUE = 0
val OPTION_SKY_STARRY = 1
val OPTION_SKY_EVENING = 2
val OPTION_SKY_RANDOM = 3

val OPTION_WIND_NONE = 0
val OPTION_WIND_LIGHT = 1
val OPTION_WIND_MEDIUM = 2
val OPTION_WIND_STRONG = 3

val OPTION_DECO_NONE = 0
val OPTION_DECO_CHRISTMAS = 1

val OPTION_GROUNDSIZE_SMALL = 0
val OPTION_GROUNDSIZE_MEDIUM = 1
val OPTION_GROUNDSIZE_LARGE = 2

class MenuScene() : GameScene(Color(77, 83, 128), gameResX, gameResY) {

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
            GameController.numberOfPlayersOption = numPlayersSelected

            EditPlayersScene(GameController.numberOfPlayersOption)
        }),
        NumberSelectorMenuPoint("Players", this, numPlayersSelected, 2, 10, onChange = {_,new ->
            numPlayersSelected = new
        }),
        NumberSelectorMenuPoint("Rounds", this, numGamesSelected, 1, 99, onChange = {_,new ->
            numGamesSelected = new
        }),
        ChangeSceneMenuPoint("Settings", this, { SettingsScene() }),
        ChangeSceneMenuPoint("About", this, { AboutScene() }),
        ExitGameMenuPoint("Exit", this)
    )
    val menuGameObject = MenuGameObject(
        this,
        Pos2D(30.0, 100.0),
        200,
        300,
        30.0,
        leftMargin = 20.0,
        menuPoints,
        onEscapePressed =
        {
            DelayedAction(this, 0.5) {
                unload()
                GameRunner.exitGame = true
            }
        })
    val hersiTanksText = HersiTanksTextGameObject(this, Pos2D(300,100), size = 2f)

    init {
        repeat(10) {
            add(FloatingBlob(this))
        }
        add(menuGameObject)
        add(hersiTanksText)
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

