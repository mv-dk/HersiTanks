package Engine

import nextId
import java.awt.Graphics2D

abstract class GameObject2(parent: IGameScene, position: Pos2D) : IGameObject {
    override val id = nextId()
    override abstract fun update()
    override abstract fun draw(g: Graphics2D)
}