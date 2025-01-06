package Experimental.Menu.MenuPoints

import Engine.IGameScene
import gameWindow

class ToggleFullScreenMenuPoint(parent: IGameScene) : MenuPointGameObject("Toggle fullscreen", parent,
    onActivate = {
        gameWindow?.toggleFullScreen()
    })