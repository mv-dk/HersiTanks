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
        val newSmokes = oldPos.stepsTo(position, 10)
        newSmokes.forEach { parent.add(ProjectileTrail(parent, it)) }

        velocity.y += 1

        if (position.y > parent.height) {
            explode()
        } else if (position.x >= terrain.position.x && position.x < terrain.rasterImage.width && position.y >= 0) {
            if (terrain.rasterImage.getRGB(position.x.toInt(), position.y.toInt()) != 0) {
                explode()
            }
        }
    }

    fun explode() {
        val activeWeapon = GameController.getCurrentPlayersTank()?.activeWeaponIdx
        if (activeWeapon == null) return
        Weapon.allWeapons[activeWeapon].onExplode(terrain, parent, this)
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