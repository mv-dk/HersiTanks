package Experimental.Menu

import Engine.*
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class MenuGameScene(override val width: Int, override val height: Int, color: Color) : GameScene(color, width, height) {

    val menuPoints = mutableListOf(
        MenuPointGameObject("Settings", this, Pos2D(100.0, 100.0)),
        MenuPointGameObject("Players", this, Pos2D(100.0, 120.0)),
        MenuPointGameObject("Go!", this, Pos2D(100.0, 140.0))
    )
    var selected: Int = 0
        get() {
            return field
        }
        set(idx) {
            if (idx < 0 || idx >= menuPoints.size) throw Exception("Cannot set selected menu point idx to $idx, because menuPoints.size == ${menuPoints.size}")
            menuPoints[selected].selected = false
            field = idx
            menuPoints[field].selected = true
        }

    init {
        menuPoints.forEach { add(it) }
        selected = 0
    }

    override fun load() {
        println("MenuGameScene.load")
    }

    override fun keyTyped(e: KeyEvent?) = Unit

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_DOWN) {
            selected = (selected + 1) % menuPoints.size
        } else if (e?.keyCode == KeyEvent.VK_UP) {
            selected = if (selected == 0) menuPoints.size-1 else selected - 1
        }
    }

    override fun keyReleased(e: KeyEvent?) = Unit

    override fun unload() {
        println("MenuGameScene.unload")

    }

}

class MenuPointGameObject(val text: String, parent: IGameScene, val position: Pos2D): GameObject2(parent, position) {
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

    override fun unload() = Unit
    override fun onAdded() = Unit
    override fun onBeforeRemoved() = Unit
    override fun onAfterRemoved() = Unit
}
