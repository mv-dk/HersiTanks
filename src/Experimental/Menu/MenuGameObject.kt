package Experimental.Menu

import Engine.*
import Experimental.Menu.MenuPoints.MenuPointGameObject
import Experimental.Menu.MenuPoints.NumberSelectorMenuPoint
import Experimental.Menu.MenuPoints.OptionSelectorMenuPoint
import Experimental.Menu.MenuPoints.TextInputMenuPoint
import SND_BAMBOO_01
import SND_BAMBOO_02
import SND_SWOOSH
import SND_SWOOSH2
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class MenuGameObject(
    parent: IGameScene,
    position: Pos2D,
    var width: Int,
    var height: Int,
    var ySpacing : Double = 40.0,
    var menuPoints: MutableList<MenuPointGameObject>,
    val onEscapePressed: () -> Unit) :
    GameObject2(parent, position) {
    var x: Double = 120.0
    var y: Double = position.y
    override var drawOrder = -1

    fun nextMenuPointPos(): Pos2D {
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
            AudioHelper.play(SND_BAMBOO_02)
            selectedIdx = (selectedIdx + 1) % menuPoints.size
        } else if (e?.keyCode == KeyEvent.VK_UP) {
            AudioHelper.play(SND_BAMBOO_01)
            selectedIdx = if (selectedIdx == 0) menuPoints.size-1 else selectedIdx - 1
        } else if (e?.keyCode == KeyEvent.VK_LEFT) {
            (selected as? NumberSelectorMenuPoint)?.decrease()
            (selected as? OptionSelectorMenuPoint)?.decrease()
        } else if (e?.keyCode == KeyEvent.VK_RIGHT){
            (selected as? NumberSelectorMenuPoint)?.increase()
            (selected as? OptionSelectorMenuPoint)?.increase()
        } else if (e?.keyCode == KeyEvent.VK_BACK_SPACE) {
            (selected as? TextInputMenuPoint)?.apply{
                if (textValue.length > 0) {
                    textValue = textValue.substring(0, textValue.length - 1)
                }
            }
        } else if (e?.keyCode == KeyEvent.VK_ENTER){
            AudioHelper.play(SND_SWOOSH)
            selected.onActivate()
        } else if (e?.keyCode == KeyEvent.VK_ESCAPE) {
            AudioHelper.play(SND_SWOOSH2)
            onEscapePressed()
        }
    }

    fun keyReleased(e: KeyEvent?) = Unit

    override fun update() = Unit

    override fun draw(g: Graphics2D) {
        g.color = Color(80, 10, 40)
        g.setStroke(BasicStroke(3f))
        g.drawRect(position.x.toInt(), position.y.toInt(), width, height)
        g.color = Color(200, 200, 200)
        g.fillRect(position.x.toInt() + 2, position.y.toInt() + 2, width-3, height-3)
    }
}