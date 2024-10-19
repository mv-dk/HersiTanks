package Engine

import nextId
import java.awt.Graphics2D
import java.awt.geom.Point2D

abstract class GameObject(parent: IGameScene, position: Point2D.Float) : IGameObject {
    override val id = nextId()
    override var drawOrder: Int = 0
    override abstract fun update()
    override abstract fun draw(g: Graphics2D)
}