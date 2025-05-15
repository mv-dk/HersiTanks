package Game.Menu

import Engine.*
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class FloatingBlob(parent: GameScene) : GameObject2(parent, Pos2D(0.0, 0.0)) {
    val size = Random.nextInt(120, 560)
    val velocity = Vec2D(
        Random.nextDouble(-1.0, 1.0),
        Random.nextDouble(-1.0, 1.0)
    )

    private val colorMargin = 20
    var color = Color(
        Random.nextInt(max(0, parent.color.red - colorMargin), min(255,parent.color.red + colorMargin)),
        Random.nextInt(max(0, parent.color.green - colorMargin), min(255, parent.color.green + colorMargin)),
        Random.nextInt(max(0, parent.color.blue - colorMargin), min(255,parent.color.blue + colorMargin))
    )

    init {
        drawOrder = -100
        position.x = Random.nextDouble(-size/2.0, parent.width + size/2.0)
        position.y = Random.nextDouble(-size/2.0, parent.height + size/2.0)
    }

    var tick = 0

    override fun update() {
        tick += 1
        position.x += velocity.x
        position.y += velocity.y

        velocity.x *= 0.99
        velocity.y *= 0.99

        if (position.x < -size/2.0) {
            position.x = parent.width + size/2.0
        } else if (position.x > parent.width + size/2.0) {
            position.x = -size/2.0
        }
        if (position.y < -size/2.0) {
            position.y = parent.height + size/2.0
        } else if (position.y >= parent.height + size/2.0) {
            position.y = -size/2.0
        }

        if (tick % size == 0) {
            velocity.x = Random.nextDouble(-1.0, 1.0)
            velocity.y = Random.nextDouble(-1.0, 1.0)
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillArc((position.x - size/2).toInt(), (position.y - size/2).toInt(), size.toInt(), size.toInt(), 0, 360)
    }
}