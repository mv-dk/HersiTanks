package Experimental.Menu.MenuPoints

import Engine.GameObject2
import Engine.GameRunner
import Engine.IGameScene
import Engine.Pos2D
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

open class MenuPointGameObject(
    var text: String,
    parent: IGameScene,
    var shadow: Boolean = true,
    var cursor: Boolean = false,
    var fontSize: Int = 24,
    var blinkWhenActive: Boolean = false,
    val onActivate: () -> Unit
): GameObject2(parent, Pos2D(0.0, 0.0)) {
    var selected: Boolean = false
        set(value) {
            field = value
            if (value) onSelected()
            else onDeselected()
        }

    var selectedColor = Color(200, 0, 180)
    var unselectedColor = Color(80, 10, 40)
    var shadowColor = Color(48, 48, 48)
    private var selectedFont = Font("Helvetica", Font.BOLD, fontSize)
    private var unselectedFont = Font("Helvetica", Font.PLAIN, fontSize)
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
        if (blinkWhenActive && tick < GameRunner.fps / 2) return

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
        g.drawString(text + if (selected && cursor && tick < GameRunner.fps /2) "|" else "", position.x.toFloat(), position.y.toFloat())
    }
}