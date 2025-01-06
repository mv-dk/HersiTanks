package Experimental.EditPlayers

import Engine.GameScene
import Engine.Pos2D
import Experimental.Menu.MenuGameObject
import Experimental.Menu.MenuGameScene
import Experimental.Menu.MenuPoints.MenuPointGameObject
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.Menu.MenuPoints.TextInputMenuPoint
import Experimental.TerrainScene.TerrainGameScene
import Game.GameController
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.event.KeyEvent

class EditPlayers() : GameScene(Color(123, 129, 78), gameResX, gameResY) {
    private val menuPoints = mutableListOf<MenuPointGameObject>()
    init {
        for (p in GameController.players) {
            val newMenuPoint = TextInputMenuPoint("Name", this, Pos2D(0.0, 0.0), p.name, p.color, 18)
            newMenuPoint.unselectedColor = Color.BLACK
            newMenuPoint.selectedColor = p.color
            menuPoints.add(newMenuPoint)
        }
        menuPoints.add(ChangeSceneMenuPoint("Start!", this, {
            menuPoints.forEachIndexed { idx, menuPoint ->
                if (menuPoint is TextInputMenuPoint) {
                    GameController.players[idx].name = menuPoint.textValue
                }
            }

            TerrainGameScene(GameController.groundSize)
        }))
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
        if (e.keyCode == KeyEvent.VK_ESCAPE) {
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuGameScene()
        } else {
            menuGameObject.keyPressed(e)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        keyHasBeenReleasedOnce = true
    }

}