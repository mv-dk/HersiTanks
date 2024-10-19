package Experimental.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject

class ExitGameMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D
): MenuPointGameObject(text, parent, position)