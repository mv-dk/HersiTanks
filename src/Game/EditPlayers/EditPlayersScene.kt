package Game.EditPlayers

import Engine.*
import Game.Menu.FloatingBlob
import Game.Menu.MenuGameObject
import Game.Menu.MenuScene
import Game.Menu.Transition
import Game.TerrainScene.BattleScene
import Game.GameController
import Game.Menu.MenuPoints.*
import Game.TerrainScene.Player.Player
import Game.TerrainScene.Player.PlayerType
import Game.Team
import SND_SWOOSH
import SND_SWOOSH2
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.event.KeyEvent

class EditPlayersScene(numPlayers: Int) : GameScene(Color(123, 129, 78), gameResX, gameResY) {

    private val menuWidth = 137
    private val menuHeight = 100
    private val menuPadding = 20
    private var menuPos = Pos2D(menuPadding/2.0, menuPadding/2.0)
    private fun nextMenuPos() :Pos2D {
        val pos = menuPos.copy()
        if (menuPos.x + 2*(menuWidth + menuPadding) > gameResX) {
            menuPos.y += menuHeight
            menuPos.x = menuPadding/2.0
        } else {
            menuPos.x += menuWidth + menuPadding
        }
        return pos
    }

    private val colors = listOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.CYAN,
        Color.YELLOW,
        Color.BLACK,
        Color.WHITE,
        Color.ORANGE,
        Color.PINK,
        Color.MAGENTA,
        Color.LIGHT_GRAY
    )

    private val playerMenuBoxes = (0..< numPlayers).mapIndexed { index, it ->
        MenuGameObject(this, nextMenuPos(), menuWidth, menuHeight, 25.0, leftMargin = 10.0,
            mutableListOf(
                MenuPointGameObject("<<< >>>", this, true, false, 14, blinkWhenActive = index == 0, onActivate = {}).apply {
                    this.unselectedColor = colors[index]
                    this.selectedColor = colors[index]
                },
                OptionSelectorMenuPoint("Type", this,
                    listOf(
                        OptionValue(1, "Human"),
                        OptionValue(2, "CPU")
                    ),
                    optionIdx = 0,
                    onChange = { _, _ -> },
                    initialFontSize = 14
                ),
                TextInputMenuPoint("Name", this, "Player $index", colors[index], initialFontSize = 14, maxTextLength = 10),
            ),
            color =  colors[index].contrast(0.1),
            strokeColor = colors[index].darker(100),
            onEscapePressed = {}
        )
    }.toMutableList()

    var activeMenuIdx = 0


    init {
        playerMenuBoxes.add(
            MenuGameObject(
                this,
                Pos2D(gameResX*3.0/4.0, gameResY - 30.0),
                gameResX/2,
                50,
                0.0,
                menuPoints = mutableListOf(
                    ChangeSceneMenuPoint(
                        "Done!",
                        this,
                        {
                            GameController.players.clear()
                            GameController.teams.clear()

                            playerMenuBoxes.forEachIndexed { idx, it ->
                                if (idx == playerMenuBoxes.size - 1) return@forEachIndexed

                                val newPlayer = getPlayer(it, colors[idx])
                                GameController.teams.add(Team("Team $i", listOf(newPlayer)))
                                GameController.players.add(newPlayer)
                            }

                            BattleScene(GameController.groundSize, tanksFallFromSky = false)
                        }
                    )
                ),
                onEscapePressed = { }
            )
        )

        add(Transition(this))
    }

    fun getPlayer(menuGameObject: MenuGameObject, color: Color) : Player {
        val type = (menuGameObject.menuPoints[1] as? OptionSelectorMenuPoint)?.let {
            when (it.options[it.optionIdx].id) {
                1 -> PlayerType.LocalHuman
                else -> PlayerType.LocalCpu
            }
        } ?: PlayerType.LocalHuman
        val name = (menuGameObject.menuPoints[2] as? TextInputMenuPoint)?.textValue ?: "no name"
        return Player(name, type).apply {
            this.color = color
            weaponry.put(1, 200)
            weaponry.put(2, 25)
//              weaponry.put(3, 100)
//              weaponry.put(4, 100)
//              weaponry.put(5, 100)
//              weaponry.put(6, 100)
//              weaponry.put(7, 100)
//              weaponry.put(8, 100)
//              weaponry.put(9, 100)
        }
    }

    init {
        for (menu in playerMenuBoxes) {
            add(menu)
        }
    }

    override fun load() {
        repeat(10) {
            add(FloatingBlob(this))
        }
    }

    private var keyHasBeenReleasedOnce = false
    override fun keyTyped(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        playerMenuBoxes[activeMenuIdx].keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        if (e.keyCode == KeyEvent.VK_ESCAPE) {
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuScene()
            return
        }

        if (playerMenuBoxes[activeMenuIdx].selectedIdx == 0) {
            when (e.keyCode) {
                KeyEvent.VK_LEFT -> {
                    AudioHelper.play(SND_SWOOSH)
                    (playerMenuBoxes[activeMenuIdx].selected as? MenuPointGameObject)?.let {
                        it.blinkWhenActive = false
                        it.selectedColor = it.selectedColor.darker(100)
                    }
                    activeMenuIdx -= 1
                    if (activeMenuIdx < 0) activeMenuIdx = playerMenuBoxes.size-1
                    (playerMenuBoxes[activeMenuIdx].selected as? MenuPointGameObject)?.let {
                        it.blinkWhenActive = true
                        it.selectedColor = it.selectedColor.lighter(100)
                    }
                }
                KeyEvent.VK_RIGHT -> {
                    AudioHelper.play(SND_SWOOSH2)
                    (playerMenuBoxes[activeMenuIdx].selected as? MenuPointGameObject)?.let {
                        it.blinkWhenActive = false
                        it.selectedColor = it.selectedColor.darker(100)
                    }
                    activeMenuIdx += 1
                    if (activeMenuIdx > playerMenuBoxes.size - 1) activeMenuIdx = 0
                    (playerMenuBoxes[activeMenuIdx].selected as? MenuPointGameObject)?.let {
                        it.blinkWhenActive = true
                        it.selectedColor = it.selectedColor.lighter(100)
                    }
                }
                else -> playerMenuBoxes[activeMenuIdx].keyPressed(e)
            }
        } else {
            playerMenuBoxes[activeMenuIdx].keyPressed(e)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        keyHasBeenReleasedOnce = true
    }
}