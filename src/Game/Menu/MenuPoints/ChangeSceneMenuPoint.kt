package Game.Menu.MenuPoints

import Engine.IGameScene
import gameWindow

class ChangeSceneMenuPoint(
    text: String,
    parent: IGameScene,
    val nextScene: (()-> IGameScene)
): MenuPointGameObject(text, parent, onActivate = {
    gameWindow?.gameRunner?.currentGameScene = nextScene()
})