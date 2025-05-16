package Game.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Engine.Vec2D
import Game.GameController
import java.awt.Color
import java.awt.Graphics2D
import kotlin.random.Random

class Snowflake(parent: IGameScene, position: Pos2D) : GameObject2(parent, position) {
    val dy = Random.nextDouble(0.5, 1.0)
    var tick = 0

    override fun update() {
        tick += 1

        if (GameController.glowUp > 0 && Explosion.currentExplosions.size > 0) {
            val explosion = Explosion.currentExplosions.first()
            val distance = position.distance(explosion.position)
            if (distance < explosion.size*1.5) {
                val diff = Vec2D(explosion.position, position)
                position.x += diff.x/(distance/10)
                position.y += diff.y/(distance/10)
                return
            }
        }
        position.y += dy
        if (position.y > parent.height) {
            parent.remove(this)
            return
        }
        position.x += GameController.wind + Random.nextDouble(-0.1, 0.1)
        if (tick > 10) {
            tick = 0

            val img = (parent as BattleScene).rasterTerrain.rasterImage
            if (position.x > 0 && position.x < img.width && position.y > 0 && position.y+1 < img.height) {
                val colAtPos = img.getRGB(position.x.toInt(), position.y.toInt() + 1)
                if (colAtPos != 0) {
                    img.setRGB(position.x.toInt(), position.y.toInt(), colAtPos)
                    parent.remove(this)
                }
            }
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = Color.WHITE
        g.fillArc(position.x.toInt(), position.y.toInt(), 2, 2, 0, 360)
    }
}