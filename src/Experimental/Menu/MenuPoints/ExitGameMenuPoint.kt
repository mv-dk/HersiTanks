package Experimental.Menu.MenuPoints

import Engine.GameRunner
import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject

class ExitGameMenuPoint(
    text: String,
    parent: IGameScene
): MenuPointGameObject(text, parent, onActivate = { -> GameRunner.exitGame = true })

