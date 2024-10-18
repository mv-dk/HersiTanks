package Experimental.Menu

import Engine.Pos2D
import Engine.GameObject2
import Engine.GameScene
import Engine.IGameScene
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

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

}

class MenuPointGameObject(val text: String, parent: IGameScene, val position: Pos2D): GameObject2(parent, position) {
    var selected: Boolean = false
    val selectedColor = Color(200, 0, 180);
    var unselectedColor = Color(80,10,40);
    val selectedFont = Font("Helvetica", Font.BOLD, 24)
    val unselectedFont = Font("Helvetica", Font.PLAIN, 24)

    override fun update() {

    }

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
