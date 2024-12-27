package Experimental.Menu.MenuPoints

import Engine.GameRunner
import Engine.IGameScene
import Engine.Pos2D
import Experimental.Menu.MenuPointGameObject
import gameWindow

class ToggleFullScreenMenuPoint(parent: IGameScene) : MenuPointGameObject("Toggle fullscreen", parent,
    onActivate = {
        gameWindow?.toggleFullScreen()
    })