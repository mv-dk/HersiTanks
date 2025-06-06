package Game.TerrainScene

import Engine.*
import Game.GameController
import Game.Menu.FloatingBlob
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import kotlin.random.Random

object CloudMaker {

    fun make(parent: IGameScene, position: Pos2D, width: Int) {
        var x = 0
        var size = 4

        while (x < width) {
            parent.add(
                CloudPart(parent, Pos2D(position.x + x, position.y - size/2), size)
            )
            x += size/2

            if (x > width/2) {
                Random.nextInt(1, 4).let {
                    size -= it
                }
                if (size < 3) {
                    Random.nextInt(10, 15).let {
                        size += it
                    }
                }
            } else {
                Random.nextInt(1, 4).let {
                    size += it
                }
            }
        }
    }
}

class CloudPart(parent: IGameScene, position: Pos2D, var size: Int): GameObject2(parent, position) {
    var velocity = Vec2D(GameController.wind/10.0, 0.0)
    private var dying = false
    private var life = -1

    companion object {
        var cloudOutlineStroke = BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    }

    override fun update() {
        if (dying) {
            size -= 1
        }
        if (dying && life <= 0 || size <= 0) {
            parent.remove(this)
            return
        }

        position.x += velocity.x
        position.y += velocity.y

        velocity.x = GameController.wind/10.0

        if (position.x < -300) {
            position.x = (parent as BattleScene).terrainWidth + 300.0
        } else if (position.x > (parent as BattleScene).terrainWidth + 300) {
            position.x = -300.0
        }
        if (!dying) {
            Projectile.activeProjectiles.forEach { p ->
                val dist = position.distance(p.position)
                if (dist < size) {
                    if (size < 20) {
                        dying = true
                        life = 200

                        velocity = Vec2D(p.position, position).plus(p.velocity).times(0.1)
                    } else if (!dying) {
                        split(p.position)
                    }
                    p.velocity.y *= 0.95
                    p.velocity.x *= 0.98
                }
            }
        }
    }

    private fun split(projectilePosition: Pos2D) {
        repeat(4) {
            val cloud1 = CloudPart(
                parent,
                position.copy().plus(Vec2D(Random.nextInt(-size/2, size/2), Random.nextInt(-size/2, size/2))),
                size/2
            )
            if (cloud1.position.distance(projectilePosition) > size/2) {
                parent.add(cloud1)
            }
        }
        parent.remove(this)
    }

    override fun draw(g: Graphics2D) {
        g.color = Color.WHITE
        g.fillArc((position.x - size/2).toInt(), (position.y - size/2).toInt(), size, size, 0, 360)
        g.color = Color.LIGHT_GRAY
        g.stroke = cloudOutlineStroke
        g.drawArc((position.x - size/2).toInt(), (position.y - size/2).toInt(), size, size, 20,  -160)
    }
}