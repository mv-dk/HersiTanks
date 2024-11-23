package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Engine.Vec2D
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

class Projectile(val parent: IGameScene, var position: Pos2D, var velocity: Vec2D) : GameObject2(parent, position) {
    private val stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    var size = 3
    var terrain = (parent as TerrainGameScene).rasterTerrain

    override fun update() {
        position.x += velocity.x
        position.y += velocity.y
        velocity.y += 1

        if (position.y > parent.height) {
            explode()
        } else if (position.x >= 0 && position.x < parent.width && position.y >= 0) {
            if (terrain.rasterImage.getRGB(position.x.toInt(), position.y.toInt()) != 0) {
                explode()
            }
        }
    }

    fun explode() {
        terrain.pokeHole(position.x.toInt(), position.y.toInt())
        //terrain.startEarthquake(position.x.toInt(), position.y.toInt())
        parent.remove(this)
        terrain.crumble = true
    }

    override fun draw(g: Graphics2D) {
        if (position.x < 0 || position.x > parent.width || position.y < 0) return

        g.color = Color.LIGHT_GRAY
        g.stroke = stroke
        g.drawLine((position.x - size).toInt(),
            (position.y - size).toInt(),
            (position.x + size).toInt(),
            (position.y + size).toInt())
    }
}