package Experimental.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject

class ChangeSceneMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D,
    val nextScene: (()-> IGameScene)
): MenuPointGameObject(text, parent, position)