package Engine

import java.awt.Graphics2D

interface IGameObject{
    val id: Int
    fun update()
    fun draw(g: Graphics2D)
}