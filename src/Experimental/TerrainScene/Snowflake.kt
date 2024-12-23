package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Game.GameController
import java.awt.Color
import java.awt.Graphics2D
import kotlin.random.Random

class Snowflake(parent: IGameScene, position: Pos2D) : GameObject2(parent, position) {
    val dy = Random.nextDouble(0.5, 1.0)

    override fun update() {
        position.y += dy
        if (position.y > parent.height) {
            parent.remove(this)
            return
        }

        position.x += GameController.wind + Random.nextDouble(-0.1, 0.1)
    }

    override fun draw(g: Graphics2D) {
        g.color = Color.WHITE
        g.fillArc(position.x.toInt(), position.y.toInt(), 2, 2, 0, 360)
    }
}