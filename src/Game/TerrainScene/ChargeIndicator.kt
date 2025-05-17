package Game.TerrainScene

import Engine.*
import Game.GameController
import SND_CHARGE
import SND_FIZZLE
import java.awt.BasicStroke
import java.awt.Graphics2D
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ChargeIndicator(
    parent: IGameScene,
    position: Pos2D,
    val tank: Tank,
    val destinationPower: Int? = null
) :GameObject2(parent, position) {
    var color = tank.color
    private var lighterColor = color.lighter(100)
    var charge = 0.0 // 0 to 1000
    private var endpointX = 0
    private var endpointY = 0
    private val stroke = BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    private var sound = SND_CHARGE

    private var playFizzleSound = false
    private var tick = 0
    private var modulus = 20

    override fun onAdded() {
        super.onAdded()
        AudioHelper.play(sound)
    }

    override fun onAfterRemoved() {
        super.onAfterRemoved()
        tank.power = charge.toInt()
        GameController.getCurrentPlayer()?.fire()
        (parent as? BattleScene)?.let {
            it.updatePlayersTurnOnNextPossibleOccasion = true
        }
        AudioHelper.stop(sound)
    }

    override fun update() {
        charge = min(charge+2.6, 1000.0)
        tank.power = charge.toInt()
        endpointX = getEndpointX(tank.angle).toInt()
        endpointY = getEndpointY(tank.angle).toInt()

        if (playFizzleSound) {
            if (tick % modulus == 0) {
                AudioHelper.stop(SND_FIZZLE)
                AudioHelper.play(SND_FIZZLE)
                if (modulus > 5) {
                    modulus -= 1
                }
            }
            tick += 1
        }
    }

    private fun getEndpointX(angle: Double): Double {
        return (position.x + charge * 0.2 * cos(Math.PI * angle/180.0))
    }

    private fun getEndpointY(angle: Double): Double {
        return (position.y - charge * 0.2 * sin(Math.PI * angle/180.0))
    }

    override fun draw(g: Graphics2D) {
        g.color = lighterColor
        g.stroke = stroke
        val xs = arrayOf(
            position.x.toInt(),
            getEndpointX(tank.angle-10).toInt(),
            getEndpointX(tank.angle+10).toInt()).toIntArray()
        val ys = arrayOf(
            position.y.toInt(),
            getEndpointY(tank.angle-10).toInt(),
            getEndpointY(tank.angle+10).toInt()).toIntArray()
        g.fillPolygon(
            xs,
            ys,
            3)
        g.color = color
        g.drawPolygon(xs, ys, 3)
    }
}