package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import Engine.nextPos2D
import Game.GameController
import java.awt.Graphics2D
import kotlin.random.Random

class SnowMaker(
    parent: IGameScene,
    override var position: Pos2D,
    val width: Int) : GameObject2(parent, position) {

    init {
        repeat(400) {
            parent.add(
                Snowflake(
                    parent,
                    Pos2D(
                        x = Random.nextDouble(-width*1.0, width*2.0),
                        y = Random.nextDouble(-width*1.0, width*1.0)
                    )
                )
            )
        }
    }

    override fun update() {
        val pos = when {
            GameController.wind < 0 -> Pos2D(Random.nextDouble(0.0, width + Math.abs(GameController.wind * parent.height)), -width/2.0)
            GameController.wind > 0 -> Pos2D(Random.nextDouble(-GameController.wind * parent.height, width.toDouble()), -width/2.0)
            else -> Pos2D(Random.nextDouble(width * -1.5, width * 1.5), -width/2.0)
        }
        parent.add(
            Snowflake(
                parent,
                Pos2D(
                    x = Random.nextDouble(-(width*1.0), (width*2.0)),
                    y = -width/2.0)
            )
        )
    }

    override fun draw(g: Graphics2D) { }
}