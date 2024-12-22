package Experimental.Menu.MenuPoints

import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject

class ChangeSceneMenuPoint(
    text: String,
    parent: IGameScene,
    val nextScene: (()-> IGameScene)
): MenuPointGameObject(text, parent)