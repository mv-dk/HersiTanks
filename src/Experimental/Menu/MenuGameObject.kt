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
    var x: Double = position.x + 20.0
    var y: Double = position.y
    override var drawOrder = -1
    var color = (parent as GameScene).color.lighter(20)
    var strokeColor = (parent as GameScene).color.darker(40)// Color(80, 10, 40)
    var stroke = BasicStroke(3f)


    fun nextMenuPointPos(): Pos2D {
        y += ySpacing
        return Pos2D(x, y)
    }

    init {
        menuPoints.forEach {
            it.position = nextMenuPointPos()
            it.unselectedColor = strokeColor.lighter(30)
            it.selectedColor = strokeColor.darker(20).contrast(1.5)
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
        height = ((menuPoints.size+0.5) * ySpacing).toInt()
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

    fun selectNext() {
        AudioHelper.play(SND_SWOOSH)
        selectedIdx = (selectedIdx + 1) % menuPoints.size
    }

    fun selectPrevious() {
        AudioHelper.play(SND_SWOOSH2)
        selectedIdx = if (selectedIdx == 0) menuPoints.size-1 else selectedIdx - 1
    }

    fun decreaseValue() {
        (selected as? NumberSelectorMenuPoint)?.decrease()
        (selected as? OptionSelectorMenuPoint)?.decrease()
    }

    fun increaseValue() {
        (selected as? NumberSelectorMenuPoint)?.increase()
        (selected as? OptionSelectorMenuPoint)?.increase()
    }

    fun deleteCharacter() {
        (selected as? TextInputMenuPoint)?.apply{
            if (textValue.length > 0) {
                textValue = textValue.substring(0, textValue.length - 1)
            }
        }
    }

    fun activate() {
        AudioHelper.play(SND_BAMBOO_01)
        selected.onActivate()
    }

    fun escape() {
        AudioHelper.play(SND_BAMBOO_02)
        onEscapePressed()
    }

    fun keyPressed(e: KeyEvent?){
        if (e?.keyCode == KeyEvent.VK_DOWN) {
            selectNext()
        } else if (e?.keyCode == KeyEvent.VK_UP) {
            selectPrevious()
        } else if (e?.keyCode == KeyEvent.VK_LEFT) {
            decreaseValue()
        } else if (e?.keyCode == KeyEvent.VK_RIGHT){
            increaseValue()
        } else if (e?.keyCode == KeyEvent.VK_BACK_SPACE) {
            deleteCharacter()
        } else if (e?.keyCode == KeyEvent.VK_ENTER){
            activate()
        } else if (e?.keyCode == KeyEvent.VK_ESCAPE) {
            escape()
        }
    }

    fun keyReleased(e: KeyEvent?) = Unit

    override fun update() = Unit

    override fun draw(g: Graphics2D) {
        g.color = strokeColor
        g.setStroke(stroke)
        g.drawRect(position.x.toInt(), position.y.toInt(), width, height)
        g.color = color
        g.fillRect(position.x.toInt() + 2, position.y.toInt() + 2, width-3, height-3)
    }
}