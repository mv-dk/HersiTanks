package Experimental.Menu

import Engine.*
import Experimental.CollisionBalls.CollisionBallsGameScene
import gameWindow
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.util.*

class MenuGameScene(override val width: Int, override val height: Int, color: Color) : GameScene(color, width, height) {

    var x: Double = 200.0
    var y: Double = 100.0
    var ySpacing: Double = 40.0

    fun nextMenuPointPos(): Pos2D{
        y += ySpacing
        return Pos2D(x, y)
    }

    val menuPoints = mutableListOf(
        ChangeSceneMenuPoint("Go!", this, nextMenuPointPos(), {
            CollisionBallsGameScene(Color.LIGHT_GRAY, 800, 600)
        }),
        NumberSelectorMenuPoint("Players", this, nextMenuPointPos(), 2, 2, 10),
        MenuPointGameObject("Settings", this, nextMenuPointPos()),
        TextInputMenuPoint("Name", this, nextMenuPointPos(), "", 10),
        ExitGameMenuPoint("Exit", this, nextMenuPointPos())
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
        menuPoints.forEach { add(it) }
        selectedIdx = 0
    }

    override fun load() {
        println("MenuGameScene.load")
    }

    override fun keyTyped(e: KeyEvent?) = Unit

    override fun keyPressed(e: KeyEvent?) {
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
        } else if (e != null && ((e.keyChar in 'a'..'z') || (e.keyChar in 'A'..'Z') || e.keyChar == ' ')) {
            (selected as? TextInputMenuPoint)?.apply{
                if (e.keyChar == ' ') {
                    textValue += ' '
                } else {
                    if (e.isShiftDown) {
                        textValue += KeyEvent.getKeyText(e.keyCode).uppercase(Locale.getDefault())
                    } else {
                        textValue += KeyEvent.getKeyText(e.keyCode).lowercase(Locale.getDefault())
                    }
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

    override fun keyReleased(e: KeyEvent?) = Unit

    override fun unload() {
        println("MenuGameScene.unload")
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

class NumberSelectorMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D,
    var numberValue: Int,
    val min: Int,
    val max: Int,
    val step: Int = 1
):
    MenuPointGameObject(text, parent, position) {

    override fun draw(g: Graphics2D) {
        if (selected) {
            g.color = selectedColor
            g.font = selectedFont
            g.drawString("$text: < $numberValue >", position.x.toFloat(), position.y.toFloat())
        } else {
            g.color = unselectedColor
            g.font = unselectedFont
            g.drawString("$text: $numberValue", position.x.toFloat(), position.y.toFloat())
        }
    }

    fun increase() {
        if (numberValue < max) numberValue += step
    }

    fun decrease() {
        if (numberValue > min) numberValue -= step
    }
}

class TextInputMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D,
    textValue: String,
    val maxLength: Int
) : MenuPointGameObject(text, parent, position){

    var textValue = textValue
        set(value) {
            if (value.length <= maxLength)
                field = value
        }

    override fun draw(g: Graphics2D) {
        if (selected) {
            g.color = selectedColor
            g.font = selectedFont
        } else {
            g.color = unselectedColor
            g.font = unselectedFont
        }

        g.drawString("$text: $textValue${if (selected) "|" else ""}", position.x.toFloat(), position.y.toFloat())
    }
}

class ExitGameMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D
): MenuPointGameObject(text, parent, position)

class ChangeSceneMenuPoint(
    text: String,
    parent: IGameScene,
    position: Pos2D,
    val nextScene: (()-> IGameScene)
): MenuPointGameObject(text, parent, position)