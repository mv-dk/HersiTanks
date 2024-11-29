package Experimental.ManyBalls

import Engine.GameObject
import Engine.GameScene
import Engine.IGameScene
import Engine.translate
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.geom.Point2D
import kotlin.random.Random

class BallGameScene(windowWidth: Int, windowHeight: Int) : GameScene(Color.LIGHT_GRAY, windowWidth, windowHeight) {
    var ballsToCreate = 10000
    var interval = 30
    var counter = interval
    override fun update() {
        super.update()

        counter -= 1
        if (counter == 0) {
            counter = interval
            if (ballsToCreate > 0) {
                add(BallGameObject(this))
                ballsToCreate -= 1
            }
        }
    }

    override fun load() = Unit
    override fun keyTyped(e: KeyEvent) = Unit
    override fun keyPressed(e: KeyEvent) = Unit
    override fun keyReleased(e: KeyEvent) = Unit
}

class BallGameObject(
    private val parent: IGameScene,
    var position: Point2D.Float,
    val size: Float,
    val color: Color
): GameObject(parent, position) {
    var velocity = Point2D.Float(0f,0f)
    var life: Int = 10000

    constructor(parent: IGameScene):
            this(parent,
                Point2D.Float(
                    Random.nextInt(0, parent.width).toFloat(),
                    Random.nextInt(0, parent.height).toFloat()),
                Random.nextDouble(10.0, 40.0).toFloat(),
                Color(
                    Random.nextInt(255),
                    Random.nextInt(255),
                    Random.nextInt(255))) {
        velocity = Point2D.Float(
            -10f + 20 * Random.nextFloat(),
            -10f + 20 * Random.nextFloat())
    }


    override fun update() {
        life -= 1
        if (life == 0) {
            parent.remove(this)
            return
        }
        position.translate(velocity)

        velocity.y += 1f
        if (position.x - size/2 < 0) {
            position.x = size/2
            velocity.x *= -1
        }
        else if (position.x + size/2 > parent.width) {
            position.x = parent.width - size/2
            velocity.x *= -1
        }
        if (position.y - size/2 < 0){
            position.y = (size/2) + 1
            velocity.y *= -1
        }
        else if (position.y + size/2 > parent.height){
            position.y = parent.height - size/2
            velocity.y *= -0.8f
            //velocity.y *= -1
            if (Math.abs(velocity.y) <= 3) velocity.y = 0f
            velocity.x *= 0.8f
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillOval((position.x - size/2).toInt(), (position.y - size/2).toInt(), size.toInt(), size.toInt())
    }

    override fun unload() = Unit
    override fun onAdded() = Unit
    override fun onBeforeRemoved() = Unit
    override fun onAfterRemoved() = Unit
}

