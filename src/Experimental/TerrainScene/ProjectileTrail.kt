package Experimental.TerrainScene

import Engine.*
import Game.GameController
import java.awt.Color
import java.awt.Graphics2D

class ProjectileTrail(parent: IGameScene, position: Pos2D) : GameObject2(parent, position) {
    var size = 5.0
    var ticksLeft = ((GameRunner.fps+(Math.random()*10).toInt()) ).toInt()
    var movement = 0.2 * GameRunner.tick
    var velocity = Vec2D(-movement/2 + Math.random()*movement, -movement/2 + Math.random()*movement)
    val baseColor = GameController.getCurrentPlayer().color.apply {
        Color(Math.min(this.red + 100, 255), Math.min(this.green + 100, 255), Math.min(this.blue + 100, 255))
    }

    override fun update() {
        if (ticksLeft <= 0) {
            parent.remove(this)
        }
        position.x += velocity.x * 60
        position.y += velocity.y * 60
        size -= 5 * GameRunner.tick
        ticksLeft -= 1
    }

    override fun draw(g: Graphics2D) {
        g.color = Color(baseColor.red, baseColor.green, baseColor.blue, Math.min(ticksLeft*2, 255))
        g.fillOval((position.x  -size/2).toInt(), (position.y - size/2).toInt(), size.toInt(), size.toInt())
    }
}