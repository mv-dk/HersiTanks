package Experimental.Menu

import Engine.*
import Experimental.CollisionBalls.CollisionBallsGameScene
import Experimental.EditPlayers.EditPlayers
import Experimental.Menu.MenuPoints.*
import Experimental.TerrainScene.TerrainGameScene
import Game.GameController
import Game.Player
import Game.Team
import gameResX
import gameResY
import gameWindow
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class MenuGameScene(override val width: Int, override val height: Int, color: Color) : GameScene(color, width, height) {
    var numPlayersSelected = 2
    var numGamesSelected = 10

    val menuPoints = mutableListOf(
        ChangeSceneMenuPoint("Go!", this, {
            //CollisionBallsGameScene(Color.LIGHT_GRAY, 800, 600)
            //TerrainGameScene(parent, Color(113,136, 248), gameResX, gameResY)
            GameController.teams.clear()
            GameController.players.clear()
            GameController.gamesToPlay = numGamesSelected
            GameController.gamesPlayed = 0
            val colors = listOf(Color.RED, Color.BLUE, Color.CYAN, Color.YELLOW, Color.BLACK, Color.WHITE, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.LIGHT_GRAY)
            for (i in 1 .. numPlayersSelected) {
                val newPlayer = Player("Player $i")
                newPlayer.color = colors[i-1]
                GameController.teams.add(Team("Team $i", listOf(newPlayer)))
                GameController.players.add(newPlayer)
            }
            EditPlayers()
        }),
        NumberSelectorMenuPoint("Players", this, 2, 2, 10, onChange = {_,new ->
            numPlayersSelected = new
        }),
        NumberSelectorMenuPoint("Rounds", this, 10, 1, 99, onChange = {_,new ->
            numGamesSelected = new
        }),
        //MenuPointGameObject("Settings", parent, nextMenuPointPos()),
        ToggleFullScreenMenuPoint(this),
        //TextInputMenuPoint("Name", parent, nextMenuPointPos(), "", 10),
        ExitGameMenuPoint("Exit", this)
    )
    val menuGameObject = MenuGameObject(this, Pos2D(100.0, 20.0), 300, 400, 40.0, menuPoints)

    init {
        add(menuGameObject)
    }

    override fun load() {
        println("MenuGameScene.load")
    }

    override fun keyTyped(e: KeyEvent) {
        menuGameObject.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        menuGameObject.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) = Unit
}

class MenuGameObject(parent: IGameScene, position: Pos2D, var width: Int, var height: Int, var ySpacing : Double = 40.0, var menuPoints: MutableList<MenuPointGameObject>) :GameObject2(parent, position) {
    var x: Double = 120.0
    var y: Double = position.y
    override var drawOrder = -1

    fun nextMenuPointPos(): Pos2D{
        y += ySpacing
        return Pos2D(x, y)
    }

    init {
        menuPoints.forEach {
            it.position = nextMenuPointPos()
        }
    }

    val selected: MenuPointGameObject
        get() {
            return menuPoints[selectedIdx]
        }

    var selectedIdx: Int = 0
        get() {
            return field
        }
        set(idx) {
            if (idx < 0 || idx >= menuPoints.size) throw Exception("Cannot set selectedIdx menu point idx to $idx, because menuPoints.size == ${menuPoints.size}")
            menuPoints[selectedIdx].selected = false
            field = idx
            menuPoints[field].selected = true
        }

    init {
        menuPoints.forEach { parent.add(it) }
        selectedIdx = 0
        height = ((menuPoints.size+1) * ySpacing).toInt()
    }

    fun keyTyped(e: KeyEvent?){
        if (e != null) {
            (selected as? TextInputMenuPoint)?.apply{
                if (e.keyChar == '\b') {
                    textValue = textValue.substring(0, textValue.length)
                } else {
                    textValue += e.keyChar
                }
            }
        }
    }

    fun keyPressed(e: KeyEvent?){
        if (e?.keyCode == KeyEvent.VK_DOWN) {
            selectedIdx = (selectedIdx + 1) % menuPoints.size
        } else if (e?.keyCode == KeyEvent.VK_UP) {
            selectedIdx = if (selectedIdx == 0) menuPoints.size-1 else selectedIdx - 1
        } else if (e?.keyCode == KeyEvent.VK_LEFT) {
            (selected as? NumberSelectorMenuPoint)?.decrease()
        } else if (e?.keyCode == KeyEvent.VK_RIGHT){
            (selected as? NumberSelectorMenuPoint)?.increase()
        } else if (e?.keyCode == KeyEvent.VK_BACK_SPACE) {
            (selected as? TextInputMenuPoint)?.apply{
                if (textValue.length > 0) {
                    textValue = textValue.substring(0, textValue.length - 1)
                }
            }
        } else if (e?.keyCode == KeyEvent.VK_ENTER){
            if ((selected as? ExitGameMenuPoint)?.selected == true) {
                GameRunner.exitGame = true
            } else if ((selected as? ChangeSceneMenuPoint)?.selected == true) {

                gameWindow?.gameRunner?.currentGameScene = (selected as ChangeSceneMenuPoint).nextScene()
            } else if ((selected as? ToggleFullScreenMenuPoint)?.selected == true) {
                gameWindow?.toggleFullScreen()
            }
        } else if (e?.keyCode == KeyEvent.VK_ESCAPE) {
            GameRunner.exitGame = true
        }
    }

    fun keyReleased(e: KeyEvent?) = Unit

    override fun update() = Unit

    override fun draw(g: Graphics2D) {
        g.color = Color(80, 10, 40)
        g.setStroke(BasicStroke(3f))
        g.drawRect(position.x.toInt(), position.y.toInt(), width, height)
        g.color = Color(200,200,200)
        g.fillRect(position.x.toInt() + 2, position.y.toInt() + 2, width-3, height-3)
    }
}

open class MenuPointGameObject(
    var text: String,
    parent: IGameScene,
    var shadow: Boolean = true,
    var cursor: Boolean = false,
    open var fontSize: Int = 24
): GameObject2(parent, Pos2D(0.0, 0.0)) {
    var selected: Boolean = false
        set(value) {
            field = value
            if (value) onSelected()
            else onDeselected()
        }
    var selectedColor = Color(200, 0, 180);
    var unselectedColor = Color(80,10,40);
    var shadowColor = Color(48, 48, 48)
    var selectedFont = Font("Helvetica", Font.BOLD, fontSize)
    var unselectedFont = Font("Helvetica", Font.PLAIN, fontSize)
    private var tick = 0

    open fun onSelected() = Unit
    open fun onDeselected() = Unit

    override fun update() {
        tick = (tick + 1) % GameRunner.fps.toInt()
    }

    fun getFont() : Font {
        return if (selected) selectedFont else unselectedFont
    }

    override fun draw(g: Graphics2D) {
        g.font = getFont()

        if (shadow) {
            g.color = shadowColor
            g.drawString(text, position.x.toFloat()+1f, position.y.toFloat()+1f)
        }

        if (selected){
            g.color = selectedColor
        } else {
            g.color = unselectedColor
        }
        g.drawString(text + if (selected && cursor && tick < GameRunner.fps/2) "|" else "", position.x.toFloat(), position.y.toFloat())
    }
}

