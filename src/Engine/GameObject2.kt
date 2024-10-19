package Engine

import nextId
import java.awt.Graphics2D

abstract class GameObject2(parent: IGameScene, position: Pos2D) : IGameObject {
    override val id = nextId()
    override var drawOrder = 1

    override abstract fun update()
    override abstract fun draw(g: Graphics2D)
    override fun unload() = Unit
    override fun onAdded() = Unit
    override fun onBeforeRemoved() = Unit
    override fun onAfterRemoved() = Unit
}