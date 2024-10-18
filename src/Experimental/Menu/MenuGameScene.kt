package Experimental.Menu

import Engine.*
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class MenuGameScene(override val width: Int, override val height: Int, color: Color) : GameScene(color, width, height) {

    val menuPoints = mutableListOf(
        MenuPointGameObject("Settings", this, Pos2D(100.0, 100.0)),
        NumberSelectorMenuPoint("Players", this, Pos2D(100.0, 120.0), 2, 2, 10),
        MenuPointGameObject("Go!", this, Pos2D(100.0, 140.0))
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

    override fun update() = Unit

    override fun draw(g: Graphics2D) {
        if (selected) {
            g.color = selectedColor
            g.font = selectedFont
        } else {
            g.color = unselectedColor
            g.font = unselectedFont
        }

        g.drawString("$text: $numberValue", position.x.toFloat(), position.y.toFloat())
    }

    fun increase() {
        if (numberValue < max) numberValue += step
    }

    fun decrease() {
        if (numberValue > min) numberValue -= step
    }
}
