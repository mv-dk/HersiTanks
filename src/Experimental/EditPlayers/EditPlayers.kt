package Experimental.EditPlayers

import Engine.GameScene
import Engine.GameWindow
import Engine.Pos2D
import Experimental.Menu.MenuGameObject
import Experimental.Menu.MenuGameScene
import Experimental.Menu.MenuPointGameObject
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.Menu.MenuPoints.TextInputMenuPoint
import Experimental.TerrainScene.TerrainGameScene
import Game.GameController
import gameResX
import gameResY
import gameWindow
import jdk.javadoc.internal.doclets.toolkit.util.DocPath.parent
import menuGameScene
import java.awt.Color
import java.awt.event.KeyEvent

class EditPlayers() : GameScene(Color(123, 129, 78), gameResX, gameResY) {
    val menuPoints = mutableListOf<MenuPointGameObject>()
    init {
        for (p in GameController.players) {
            val newMenuPoint = TextInputMenuPoint("Name", this, Pos2D(0.0, 0.0), p.name, 10, p.color, 18)
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

            TerrainGameScene(this, Color(113,136, 248), gameResX, gameResY)
        }))
    }

    val menuGameObject = MenuGameObject(this, Pos2D(100.0, 20.0), 300, 400, 25.0, menuPoints)
    init {
        add(menuGameObject)
    }

    override fun load() { }

    var keyHasBeenReleasedOnce = false
    override fun keyTyped(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        menuGameObject.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        if (e.keyCode == KeyEvent.VK_ESCAPE) {
            unload()
            gameWindow?.gameRunner?.currentGameScene = menuGameScene
        } else {
            menuGameObject.keyPressed(e)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        keyHasBeenReleasedOnce = true
    }

}