package Experimental.Menu

import Engine.*
import Experimental.CollisionBalls.CollisionBallsGameScene
import Experimental.Menu.MenuPoints.ChangeSceneMenuPoint
import Experimental.Menu.MenuPoints.ExitGameMenuPoint
import Experimental.Menu.MenuPoints.NumberSelectorMenuPoint
import Experimental.Menu.MenuPoints.TextInputMenuPoint
import Experimental.TerrainScene.TerrainGameScene
import gameWindow
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class MenuGameScene(override val width: Int, override val height: Int, color: Color) : GameScene(color, width, height) {

    val menuGameObject = MenuGameObject(this, Pos2D(100.0, 100.0), 300, 400)

    init {
        add(menuGameObject)
    }

    override fun load() {
        println("MenuGameScene.load")
    }

    override fun keyTyped(e: KeyEvent?) {
        menuGameObject.keyTyped(e)

    }

    override fun keyPressed(e: KeyEvent?) {
        menuGameObject.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent?) = Unit
}

class MenuGameObject(val parent: IGameScene, val position: Pos2D, var width: Int, var height: Int) :GameObject2(parent, position) {
    var x: Double = 120.0
    var y: Double = 100.0
    var ySpacing: Double = 40.0
    override var drawOrder = -1

    fun nextMenuPointPos(): Pos2D{
        y += ySpacing
        return Pos2D(x, y)
    }

    val menuPoints = mutableListOf(
        ChangeSceneMenuPoint("Go!", parent, nextMenuPointPos(), {
            CollisionBallsGameScene(Color.LIGHT_GRAY, 800, 600)
            //TerrainGameScene(parent, Color.LIGHT_GRAY, 800, 600)
        }),
        NumberSelectorMenuPoint("Players", parent, nextMenuPointPos(), 2, 2, 10),
        MenuPointGameObject("Settings", parent, nextMenuPointPos()),
        TextInputMenuPoint("Name", parent, nextMenuPointPos(), "", 10),
        ExitGameMenuPoint("Exit", parent, nextMenuPointPos())
    )
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
                GameWindow.exitGame = true
            } else if ((selected as? ChangeSceneMenuPoint)?.selected == true) {
                gameWindow?.currentGameScene = (selected as ChangeSceneMenuPoint).nextScene()
            }
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

open class MenuPointGameObject(var text: String, parent: IGameScene, val position: Pos2D): GameObject2(parent, position) {
    var selected: Boolean = false
    val selectedColor = Color(200, 0, 180);
    var unselectedColor = Color(80,10,40);
    val selectedFont = Font("Helvetica", Font.BOLD, 24)
    val unselectedFont = Font("Helvetica", Font.PLAIN, 24)

    override fun update() = Unit

    override fun draw(g: Graphics2D) {
        if (selected){
            g.color = selectedColor
            g.font = selectedFont
        } else {
            g.color = unselectedColor
            g.font = unselectedFont
        }
        g.drawString(text, position.x.toFloat(), position.y.toFloat())
    }
}

