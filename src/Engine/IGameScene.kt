package Engine

import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseListener

interface IGameScene {
    fun update()
    fun draw(g: Graphics2D)
    fun add(gameObject: IGameObject)
    fun remove(gameObject: IGameObject)
    fun load()
    fun unload()
    fun forEachGameObject(act: (obj: IGameObject) -> Unit)
    fun keyPressed(e: KeyEvent)
    fun keyReleased(e: KeyEvent)
    fun keyTyped(e: KeyEvent)
    val width: Int
    val height: Int
}