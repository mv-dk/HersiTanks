package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Engine.Vec2D
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

class Projectile(val parent: IGameScene, var position: Pos2D, var velocity: Vec2D) : GameObject2(parent, position) {
    private val stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    var size = 3
    var terrain = (parent as TerrainGameScene).rasterTerrain

    init {
        GameController.projectilesFlying += 1
    }

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
        when (GameController.getCurrentPlayersTank()?.activeWeapon) {
            WEAPON_BOMB -> {
                val holeSize = random.nextInt(30, 200)
                parent.remove(this)
                val exp = Explosion(parent, position, holeSize, holeSize / 3, {
                    terrain.crumble = true
                })
                GameController.projectilesFlying -= 1
                parent.add(exp)
            }
            WEAPON_EARTHQUAKE -> {
                terrain.startEarthquake(position.x.toInt(), position.y.toInt())
                parent.remove(this)
                GameController.projectilesFlying -= 1
            }
            else -> {
                parent.remove(this)
                GameController.projectilesFlying -= 1
                println("MEH")
            }
        }
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