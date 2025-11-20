package Game.EditPlayers

import Engine.*
import Engine.Audio.AudioHelper
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
import SND_FIZZLE
import SND_SWOOSH
import SND_SWOOSH2
import gameResX
import gameResY
import gameWindow
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class EditPlayersScene(numPlayers: Int) : GameScene(Color(123, 129, 78), gameResX, gameResY) {

    private val menuWidth = 193
    private val menuHeight = 80
    private val menuPadding = 20
    private var menuPos = Pos2D(menuPadding/2.0, menuPadding/2.0)
    private fun nextMenuPos() :Pos2D {
        val pos = menuPos.copy()
        if (menuPos.x + 2*(menuWidth) + menuPadding > gameResX) {
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
        Color(96,38,0),
        Color.WHITE,
        Color.ORANGE,
        Color.PINK,
        Color.MAGENTA,
        Color.LIGHT_GRAY
    )

    private val menuPoints = (0..< numPlayers).mapIndexed { index, it ->
        MenuGameObject(this, nextMenuPos(), menuWidth, menuHeight, 25.0, leftMargin = 10.0,
            mutableListOf(
                TextInputMenuPoint(
                    "Name",
                    this,
                    "Player $index",
                    colors[index],
                    initialFontSize = 14,
                    maxTextLength = 10,
                    maxTextLengthIsMs = true
                ).apply {
                    cursor = false
                },
                OptionSelectorMenuPoint(
                    "Type",
                    this,
                    listOf(
                        OptionValue(1, "Human"),
                        OptionValue(2, "CPU")
                    ),
                    optionIdx = 0,
                    onChange = { _, _ -> },
                    initialFontSize = 14
                ),
            ),
            color =  colors[index].contrast(0.1),
            strokeColor = colors[index].darker(100),
            onEscapePressed = {}
        )
    }.toMutableList()

    private var activeMenuIdx = 0

    init {
        menuPoints.add(
            MenuGameObject(
                this,
                Pos2D(gameResX*3.0/4.0, gameResY - 30.0),
                gameResX/2,
                50,
                0.0,
                menuPoints = mutableListOf(
                    ChangeSceneMenuPoint(
                        "Done!",
                        this
                    ) {
                        GameController.players.clear()
                        GameController.teams.clear()

                        menuPoints.forEachIndexed { idx, it ->
                            if (idx == menuPoints.size - 1) return@forEachIndexed

                            val newPlayer = getPlayer(it, colors[idx])
                            GameController.teams.add(Team("Team $i", listOf(newPlayer)))
                            GameController.players.add(newPlayer)
                        }

                        BattleScene(GameController.groundSize, tanksFallFromSky = false)
                    }
                ),
                onEscapePressed = { }
            )
        )

        add(Transition(this))
        selectPlayerMenuBox(currentlySelectedMenuPoint)
    }

    fun getPlayer(menuGameObject: MenuGameObject, color: Color) : Player {
        val type = (menuGameObject.menuPoints[1] as? OptionSelectorMenuPoint)?.let {
            when (it.options[it.optionIdx].id) {
                1 -> PlayerType.LocalHuman
                else -> PlayerType.LocalCpu
            }
        } ?: PlayerType.LocalHuman
        val name = (menuGameObject.menuPoints[0] as? TextInputMenuPoint)?.textValue ?: "no name"
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
            fuel = 100.0
        }
    }

    init {
        for (menu in menuPoints) {
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
        // When detecting tab in keyTyped, for some reason we have to check
        // for the typed character being a tab character, instead of just
        // comparing VK_TAB (which has the value 9) with the keyCode (which
        // has the value 0 for some reason!)
        if (e.keyChar != '\t') {
            currentlySelectedMenuPoint.keyTyped(e)
        }
    }

    private fun unselectPlayerMenuBox(box: MenuGameObject) {
        box.stroke = BasicStroke(3f)
        box.color = box.color.darker(40)
        (box.selected as? MenuPointGameObject)?.let {
            it.cursor = false
        }
    }

    private fun selectPlayerMenuBox(box: MenuGameObject) {
        box.stroke = BasicStroke(6f)
        box.color = box.color.lighter(40)
        (box.selected as? MenuPointGameObject)?.let {
            it.cursor = true
        }
    }

    private val currentlySelectedMenuPoint
        get() = menuPoints[activeMenuIdx]

    override fun keyPressed(e: KeyEvent) {
        if (!keyHasBeenReleasedOnce) return
        if (e.keyCode == KeyEvent.VK_ESCAPE) {
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuScene()
            return
        }

        when (e.keyCode) {
            KeyEvent.VK_LEFT -> {
                AudioHelper.play(SND_SWOOSH)
                unselectPlayerMenuBox(currentlySelectedMenuPoint)
                activeMenuIdx -= 1
                if (activeMenuIdx < 0) activeMenuIdx = menuPoints.size - 1
                selectPlayerMenuBox(currentlySelectedMenuPoint)
            }
            KeyEvent.VK_RIGHT -> {
                AudioHelper.play(SND_SWOOSH)
                unselectPlayerMenuBox(currentlySelectedMenuPoint)
                activeMenuIdx = (activeMenuIdx + 1) % menuPoints.size
                selectPlayerMenuBox(currentlySelectedMenuPoint)
            }
            KeyEvent.VK_DOWN -> {
                AudioHelper.play(SND_SWOOSH)
                unselectPlayerMenuBox(currentlySelectedMenuPoint)
                activeMenuIdx = (activeMenuIdx + 3)
                if (activeMenuIdx > menuPoints.size - 1) activeMenuIdx = menuPoints.size - 1
                selectPlayerMenuBox(currentlySelectedMenuPoint)
            }
            KeyEvent.VK_UP -> {
                AudioHelper.play(SND_SWOOSH2)
                unselectPlayerMenuBox(currentlySelectedMenuPoint)
                if (activeMenuIdx == menuPoints.size - 1) {
                    activeMenuIdx -= 1
                } else {
                    activeMenuIdx -= 3
                    if (activeMenuIdx < 0) activeMenuIdx = menuPoints.size - 1
                }
                selectPlayerMenuBox(currentlySelectedMenuPoint)
            }
            KeyEvent.VK_ENTER -> {
                menuPoints[activeMenuIdx].keyPressed(e)
            }
            KeyEvent.VK_TAB -> {
                if (activeMenuIdx == menuPoints.size - 1) { currentlySelectedMenuPoint.keyPressed(e) }
                else {
                    (currentlySelectedMenuPoint.menuPoints[1] as? OptionSelectorMenuPoint)?.let {
                        if (it.optionIdx == 0) it.increase() else it.decrease()
                        AudioHelper.play(SND_FIZZLE)
                    }
                }
            }
            KeyEvent.VK_BACK_SPACE -> {
                menuPoints[activeMenuIdx].keyPressed(e)
            }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        keyHasBeenReleasedOnce = true
    }

    private val hintFont = Font("Helvetica", Font.PLAIN, 14)
    override fun draw(g: Graphics2D) {
        super.draw(g)
        g.font = hintFont
        g.drawString("Change type: <tab>", 14, gameResY - 14)
    }
}