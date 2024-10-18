package Engine

import java.awt.Graphics2D

interface IGameScene {
    fun update()
    fun draw(g: Graphics2D)
    fun add(gameObject: IGameObject)
    fun remove(gameObject: IGameObject)
    fun load()
    fun unload()
    fun forEachGameObject(act: (obj: IGameObject) -> Unit)
    val width: Int
    val height: Int
}