package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Engine.Vec2D
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

open class Projectile(parent: IGameScene, position: Pos2D, var velocity: Vec2D) : GameObject2(parent, position) {
    var jumps: Int = 0
    private val stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    var size = 3
    var terrain = (parent as TerrainGameScene).rasterTerrain

    init {
        GameController.projectilesFlying += 1
    }

    override fun update() {
        val oldPos = position.copy()
        position.x += velocity.x + GameController.wind
        position.y += velocity.y

        velocity.y += 1

        if (position.y > parent.height) {
            explode()
        } else if (position.x >= terrain.position.x && position.x < terrain.rasterImage.width && position.y >= 0) {
            if (terrainAt(position)) {
                position = findExactTerrainIntersection(oldPos, position)
                explode()
            }
        }
        val newSmokes = oldPos.stepsTo(position, 10)
        newSmokes.forEach { parent.add(ProjectileTrail(parent, it)) }
    }

    private fun terrainAt(p: Pos2D): Boolean {
        return terrain.rasterImage.getRGB(p.x.toInt(), p.y.toInt()) != 0
    }

    private fun findExactTerrainIntersection(oldPos: Pos2D, newPos: Pos2D): Pos2D {
        var done = false
        var p0 = oldPos.copy()
        var p1 = position.copy()
        var pHalf = position.copy()
        pHalf.x = p0.x + (p1.x - p0.x)/2.0
        pHalf.y = p0.y + (p1.y - p0.y)/2.0

        while (!done) {
            if (terrainAt(pHalf)) {
                p1.x = pHalf.x
                p1.y = pHalf.y
                pHalf.x = p0.x + (p1.x - p0.x)/2.0
                pHalf.y = p0.y + (p1.y - p0.y)/2.0
            } else {
                p0.x = pHalf.x
                p0.y = pHalf.y
                pHalf.x = p0.x + (p1.x - p0.x)/2.0
                pHalf.y = p0.y + (p1.y - p0.y)/2.0
            }
            val dist = p0.distance(p1)
            done = dist < 2
        }
        return p0
    }

    fun explode() {
        Weapon.allWeapons[GameController.getCurrentPlayer().currentWeaponId]?.onExplode(terrain, parent, this)
    }

    override fun draw(g: Graphics2D) {
        if (position.x < 0 || position.x > parent.width || position.y < 0) return

        g.color = Color.LIGHT_GRAY
        g.stroke = stroke
        val p0 = position.plus(velocity)
        val p1 = position.plus(velocity.times(-1.0))
        g.drawLine(p0.x.toInt(), p0.y.toInt(), p1.x.toInt(), p1.y.toInt())
    }
}

class MidairExplodingProjectile(parent: IGameScene, position: Pos2D, velocity: Vec2D): Projectile(parent, position, velocity) {
    var ticks = 0
    val minTicks = 30

    override fun update() {
        super.update()
        ticks += 1
        if (ticks > minTicks) {
            if (velocity.y >= 0) {
                explode()
            }
        }
    }
}