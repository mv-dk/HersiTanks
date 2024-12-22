package Engine

import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

interface IGameScene {
    fun update()
    fun draw(g: Graphics2D)
    fun add(gameObject: IGameObject)
    fun remove(gameObject: IGameObject)
    fun load()
    fun unload()
    fun forEachGameObject(act: (obj: IGameObject) -> Unit)
    fun keyPressed(e: KeyEvent) { }
    fun keyReleased(e: KeyEvent) { }
    fun keyTyped(e: KeyEvent) { }
    fun mouseMoved(e: MouseEvent) { }
    fun mouseClicked(e: MouseEvent) { }
    fun mouseReleased(e: MouseEvent) { }
    fun mouseEntered(e: MouseEvent) { }
    fun mouseExited(e: MouseEvent) { }
    fun mousePressed(e: MouseEvent) { }
    fun mouseWheel(e: MouseEvent) { }
    fun gameObjectsCount() :Int
    val width: Int
    val height: Int
}