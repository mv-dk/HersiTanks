package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Engine.Vec2D
import Game.GameController
import java.awt.Color
import java.awt.Graphics2D

class Explosion(val parent: IGameScene, val position: Pos2D, var size: Int, val duration: Int, val onDone: () -> Unit) : GameObject2(parent, position) {
    var tick: Int = 0

    override fun update() {
        tick += 1
        if (tick ==1) {
            GameController.explosionsActive += 1
            GameController.tanks.forEach {
                val distance = Vec2D(position, it.position).mag()
                if (distance < size) {
                    val delta = 20*(size / distance).toInt()
                    it.energy -= delta
                    if (it.energy < 0) it.energy = 0
                }
            }
        }
        if (duration - tick < 10) size = (size * 0.8).toInt()
        if (tick >= duration) {
            parent.remove(this)
            GameController.explosionsActive -= 1
            onDone()
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = Color.RED
        g.fillOval((position.x - size/2).toInt(), (position.y - size/2).toInt(), size, size)
    }
}