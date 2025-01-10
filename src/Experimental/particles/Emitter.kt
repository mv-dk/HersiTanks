package Experimental.particles

import Engine.*
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Stroke
import kotlin.random.Random

abstract class Emitter(
    parent: IGameScene,
    position: Pos2D,
    val width: Int,
    emitSeconds: Double
    ) : GameObject2(parent, position) {

    var emitTicksLeft = (emitSeconds * GameRunner.fps).toInt()

    abstract fun makeParticle(emitTicksLeft: Int): Particle

    override fun update() {
        emitTicksLeft -= 1

        if (emitTicksLeft <= 0) {
            parent.remove(this)
            return
        }
        val particle = makeParticle(emitTicksLeft)

        particle.drawOrder = this.drawOrder
        parent.add(particle)
    }

    override fun draw(g: Graphics2D) = Unit
}

class FireEmitter(
    parent: IGameScene,
    position: Pos2D,
    width: Int,
    emitSeconds: Double
): Emitter(parent, position, width, emitSeconds) {

    init {
        parent.add(this)
    }

    companion object {
        val fireColors = arrayOf(
            //Color(243, 120, 10, 255),
            //Color(244, 128, 10, 255),
            Color(245,  160, 10, 255),
            Color(246,  190, 10, 255),
            Color(247, 220, 10, 255)
        )
        val fireSizes = arrayOf(
            10.0, 9.0, 8.0, 7.0, 6.0, 6.0, 5.0, 5.0, 5.0, 4.0, 3.0, 3.5, 3.0, 2.5, 2.0, 1.5, 1.0, 1.0, 1.0, 1.0,
        )
    }

    override fun makeParticle(emitTicksLeft: Int): Particle {
        val x: Double = Random.nextDouble(position.x - width/2.0, position.x + width/2.0)
        val xvel = Random.nextDouble(-0.5, 0.5)
        val extraLife = if (emitTicksLeft % 5 == 0) 0.3 else 0.0
        return Particle(
            parent,
            Pos2D(x, position.y),
            Vec2D(xvel, -1.0),
            Vec2D(-xvel/20.0 + GameController.wind/100.0, 0.0),
            Random.nextDouble(0.4, 0.6) + extraLife,
            fireColors,
            fireSizes
        )
    }
}

class SmokeEmitter(
    parent: IGameScene,
    position: Pos2D,
    width: Int,
    emitSeconds: Double):
    Emitter(parent, position, width, emitSeconds) {

    init {
        parent.add(this)
    }

    companion object {
        val smokeColors = arrayOf(
            Color(0,0,0,2),
            Color(0,0,0,4),
            Color(0,0,0,8),
            Color(0,0,0,8),
            Color(0,0,0,8),
            Color(0,0,0,8),
            Color(0,0,0,4),
            Color(0,0,0,2)
        )
        val smokeSizes = arrayOf(
            14.0, 10.0, 8.0, 6.0, 4.0, 2.0, 1.0
        )
    }

    override fun makeParticle(emitTicksLeft: Int): Particle {
        val x: Double = Random.nextDouble(position.x - width/2.0, position.x + width/2.0)
        val xvel = Random.nextDouble(-0.1, 0.1)

        val extraLife = if (emitTicksLeft % 5 == 0) 0.5 else 0.0
        return Particle(
            parent,
            Pos2D(x, position.y),
            Vec2D(xvel, Random.nextDouble(-1.0, -0.5)),
            Vec2D(GameController.wind/120.0, 0.0),
            Random.nextDouble(1.8, 2.6) + extraLife,
            smokeColors,
            smokeSizes
        )
    }

}

class Particle(
    parent: IGameScene,
    pos: Pos2D,
    startVelocity: Vec2D,
    val gravity: Vec2D,
    lifeInSeconds: Double,
    val colors: Array<Color>,
    val sizes: Array<Double>
) : GameObject2(parent, pos){

    val lifeInTicks = (GameRunner.fps * lifeInSeconds).toInt()
    var ticksLeft = lifeInTicks
    var color = colors.first()
    var size = sizes.first()

    var velocity = startVelocity.copy()

    override fun update() {
        ticksLeft -= 1

        if (ticksLeft <= 0) {
            parent.remove(this)
            return
        }

        val progress = (lifeInTicks - ticksLeft) / lifeInTicks.toDouble()
        val colIdx = (colors.size * progress).toInt()
        color = colors[colIdx]

        position.x += velocity.x
        position.y += velocity.y

        velocity.x += gravity.x
        velocity.y += gravity.y

        val sizeIdx = (sizes.size * progress).toInt()
        size = sizes[sizeIdx]
    }

    companion object {
        var strokes = mutableMapOf<Double, Stroke>()
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        val nextPos = position.plus(velocity)
        if (!strokes.containsKey(size)){
            strokes[size] = BasicStroke(size.toFloat(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        }
        g.stroke = strokes[size]
        g.drawLine(
            position.x.toInt(),
            position.y.toInt(),
            nextPos.x.toInt(),
            nextPos.y.toInt()
            )

//        g.fillOval(
//            (position.x - size/2.0).toInt(),
//            (position.y - size*2.0/3.0).toInt(),
//            size.toInt(),
//            (size*1.5).toInt())

//        g.fillArc(
//            (position.x - size/2).toInt(),
//            (position.y - size/2).toInt(),
//            size.toInt(),
//            size.toInt(),
//            0,
//            360)
    }

}