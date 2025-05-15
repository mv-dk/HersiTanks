package Game.Menu.MenuPoints

import Engine.GameRunner
import Engine.IGameScene

class ExitGameMenuPoint(
    text: String,
    parent: IGameScene
): MenuPointGameObject(text, parent, onActivate = { GameRunner.exitGame = true })

