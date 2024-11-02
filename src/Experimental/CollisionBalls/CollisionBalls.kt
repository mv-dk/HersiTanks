package Experimental.CollisionBalls

import Engine.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import kotlin.random.Random

val random = Random(1)

class CollisionBallsGameScene(color: Color, width: Int, height: Int) : GameScene(color, width, height) {
    val interval = 300
    var counter = 30
    var ballsToCreate = 30
    var balls: MutableList<CollisionBallsGameObject> = mutableListOf()

    override fun update() {
        super.update()

        counter -= 1
        if (counter == 0) {
            counter = interval
            if (ballsToCreate > 0) {
                add(
                    CollisionBallsGameObject(
                    this,
                    Pos2D(random.nextDouble(0.0, width.toDouble()), random.nextDouble(0.0, height.toDouble())),
                    Vec2D(random.nextDouble()*2, random.nextDouble()*2),
                    random.nextDouble(10.0, 30.0),
                    Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))
                )
                )
                ballsToCreate -= 1
            }
        }
    }

    override fun load() = Unit
    override fun keyTyped(e: KeyEvent?) = Unit
    override fun keyPressed(e: KeyEvent?) {
        println("Key pressed!")
    }
    override fun keyReleased(e: KeyEvent?) = Unit

    override fun add(gameObject: IGameObject) {
        super.add(gameObject)
        if (gameObject is CollisionBallsGameObject){
            balls.add(gameObject)
        }
    }

    override fun remove(gameObject: IGameObject){
        super.remove(gameObject)
        balls.remove(gameObject) // sikkert ikke helt optimalt
    }
}

class CollisionBallsGameObject(val parent: IGameScene, val position: Pos2D, var velocity: Vec2D, val size: Double, val color: Color)
    : GameObject2(parent, position) {

    override fun update() {
        (parent as CollisionBallsGameScene).balls.forEach {
            if (it.id != this.id){
                val distance = (this.position + velocity).distance(it.position + it.velocity)
                val touchDistance = this.size/2.0 + it.size/2.0
                if (distance < touchDistance) {
                    val reflector = Vec2D(this.position, it.position).hat().normalized()
                    val newVelocity = this.velocity.reflect(reflector)
                    velocity = newVelocity + velocity*0.5

//                    val d = Vec2D(this.position, it.position)
//                    val overlap = touchDistance - distance
//                    position.translate(d* overlap)
//                    it.position.translate(d* overlap)

                    it.velocity = it.velocity.reflect(reflector) + it.velocity*0.5
                }
            }
        }

        position.translate(velocity)
        if (position.x - size/2 < 0) {
            position.x = size/2
            velocity.x *= -0.9
        }
        else if (position.x + size/2 > parent.width) {
            position.x = parent.width - size/2
            velocity.x *= -0.9
        }
        if (position.y - size/2 < 0){
            position.y = (size/2) + 1
            velocity.y *= -0.9
        }
        else if (position.y + size/2 > parent.height){
            position.y = parent.height - size/2
            velocity.y *= -0.9
        }
        else {
            velocity.y += 0.1
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillOval((position.x - size/2).toInt(), (position.y - size/2).toInt(), size.toInt(), size.toInt())

        g.color = Color.BLACK
        val velocityEndpoint = position + (velocity*10.0)
        g.drawLine(position.x.toInt(), position.y.toInt(), velocityEndpoint.x.toInt(), velocityEndpoint.y.toInt())
    }

    override fun unload() = Unit
    override fun onAdded() = Unit
    override fun onBeforeRemoved() = Unit
    override fun onAfterRemoved() = Unit
}
