package Game.TerrainScene

import Engine.*
import Game.Menu.OPTION_DECO_CHRISTMAS
import Game.particles.Emitter
import Game.particles.FireEmitter
import Game.particles.SmokeEmitter
import Game.GameController
import SND_CHANGE_ANGLE
import SND_DRIVE
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Polygon
import kotlin.math.cos
import kotlin.math.sin

enum class Direction { LEFT, RIGHT }

class Tank(parent: IGameScene, var rasterTerrain: RasterTerrain, position: Pos2D, val color: Color) : GameObject2(parent, position) {
    var chargeIndicator: ChargeIndicator? = null
    var size = 20
        set(value) {
            field = value
            stroke = BasicStroke(value/10f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
            fatStroke = BasicStroke(value/5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
        }
    var power = 100
    var angle = 45.0
    var energy = 100
    private var stroke = BasicStroke(size/10f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    private var fatStroke = BasicStroke(size/5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    var falling: Boolean = false
    private var firstFall = true
    var playing = true
    var canonX = (position.x + size * cos(Math.PI*angle/180.0)).toInt()
    var canonY = (position.y - size * sin(Math.PI*angle/180.0)).toInt()
    var smokeEmitter: Emitter? = null
    var fireEmitter: Emitter? = null
    var direction = Direction.RIGHT
    var fuel = 0.0

    override fun update() {
        if (falling){
            if (position.y.toInt() < rasterTerrain.rasterImage.height-2) {
                if (rasterTerrain.rasterImage.getRGB(position.x.toInt(), position.y.toInt() + 2) == 0 ||
                    rasterTerrain.rasterImage.getRGB(position.x.toInt() - size/4, position.y.toInt() + 2) == 0 ||
                    rasterTerrain.rasterImage.getRGB(position.x.toInt() + size/4, position.y.toInt() + 2) == 0) {
                    position.y += 60 / GameRunner.fps
                    rasterTerrain.rasterImage.graphics.color = Color(0,0,0,0)
                    rasterTerrain.pokeLine((position.x-size/2).toInt(), position.y.toInt()-5, (position.x+size/2).toInt(), position.y.toInt()-5, 3f)

                    if (!firstFall && energy > 0) {
                        energy -= 1
                        GameController.getCurrentPlayer()?.let { p -> p.money += 1 }
                    }

                    onTankMoved()
                } else {
                    falling = false
                    firstFall = false
                }
            } else {
                falling = false
                firstFall = false
            }
        }
        if (energy < 50) {
            if (fireEmitter == null) {
                addFire()
            }
        }
        if (fireEmitter != null) {
            if (fireEmitter?.emitTicksLeft == 0) {
                parent.remove(fireEmitter!!)
                fireEmitter = null
            }
        }
        if (smokeEmitter != null) {
            if (smokeEmitter?.emitTicksLeft == 0) {
                parent.remove(smokeEmitter!!)
                smokeEmitter = null
            }
        }
    }

    fun addFire(){
        if (fireEmitter == null) {
            val emitter = FireEmitter(parent, this.position, 4, 5.0)
            emitter.drawOrder = -10
            fireEmitter = emitter
        }
        if (smokeEmitter == null) {
            val emitter = SmokeEmitter(parent, this.position, 4, 10.0)
            emitter.drawOrder = -11
            smokeEmitter = emitter
        }
    }

    fun onDie(){
        fireEmitter?.let {
            it.emitTicksLeft = 0
        }
        smokeEmitter?.let {
            it.emitTicksLeft = 0
        }
        fireEmitter = null
        smokeEmitter = null
    }

    override fun draw(g: Graphics2D) {
        val fillColor = if (GameController.glowUp > 0) color.lighter(GameController.glowUp*10) else color.mult(energy/100.0)
        val strokeColor = fillColor.darker(100)
        g.color = fillColor
        g.stroke = fatStroke

        g.color = strokeColor
        g.drawLine(position.x.toInt(), position.y.toInt(), canonX, canonY)

        g.stroke = stroke
        g.drawArc((position.x - size/2).toInt(), (position.y - (size/2.0) + 2).toInt(), size, size, 0, 180)
        g.color = fillColor
        g.drawLine(position.x.toInt(), position.y.toInt(), canonX, canonY)
        g.fillArc((position.x - size/2).toInt(), (position.y - (size/2.0) + 2).toInt(), size, size, 0, 180)

        //drawCenter(g)

        if (GameController.decorationOption == OPTION_DECO_CHRISTMAS) {
            drawChristmasHat(g)
        }
    }

    fun drawChristmasHat(g: Graphics2D) {
        val dir = if (angle <= 90) 1.0 else -1.0

        // Hat triangle
        g.color = Color.RED
        val polygon = Polygon(
            arrayOf(
                (position.x - dir * size/2).toInt(),
                position.x.toInt(),
                (position.x - dir * size/2).toInt()).toIntArray(),
            arrayOf(
                (position.y - size*3/4).toInt(),
                (position.y - size/2.0).toInt(),
                (position.y - size/7.0).toInt()).toIntArray(),
            3
            )
        g.fillPolygon(polygon)

        // Hat shadow
        g.color = Color.RED.darker(30)
        val shadowPolygon = Polygon(
            arrayOf(
                (position.x - dir * size/2).toInt(),
                (position.x - dir * size*2.0/20.0).toInt(),
                (position.x - dir * size*7.0/20.0).toInt()).toIntArray(),
            arrayOf(
                (position.y - size * 15.0/20.0).toInt(),
                (position.y - size * 11.0/20.0).toInt(),
                (position.y - size * 9.0/20.0).toInt()
                ).toIntArray(),
            3
        )
        g.fillPolygon(shadowPolygon)

        // Hat circle
        g.color = Color.WHITE
        g.fillOval((position.x - dir * size*11.0/20.0 - size/8.0).toInt(), (position.y - size).toInt(), (size/4.0).toInt(), (size/4.0).toInt())

        // Hat white rim
        g.stroke = BasicStroke(size/7.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
        g.drawLine(
            (position.x - dir *  size*0.55).toInt(), (position.y - size*0.1).toInt(),
            (position.x + dir * size*0.1).toInt(), (position.y - size*0.55).toInt())
    }

    fun drawCenter(g: Graphics2D){
        g.color = Color.WHITE
        g.drawLine(position.x.toInt()-1, position.y.toInt(), position.x.toInt()+1, position.y.toInt())
    }

    fun onTankMoved(pokeTerrain: Boolean = true){
        updateCanonXY(pokeTerrain)
    }

    fun onAngleChanged(){
        updateCanonXY()
    }

    fun onPowerChanged(){
    }

    // Call this every time the tank has moved, or the angle has changed
    fun updateCanonXY(pokeTerrain: Boolean = true){
        canonX = (position.x + size * cos(Math.PI*angle/180.0)).toInt()
        canonY = (position.y - size * sin(Math.PI*angle/180.0)).toInt()
        val fromX = (position.x + size*0.9 * cos(Math.PI*angle/180.0)).toInt()
        val fromY = (position.y - size*0.9 * sin(Math.PI*angle/180.0)).toInt()
        if (pokeTerrain) {
            rasterTerrain.pokeLine(fromX, fromY, canonX, canonY, 3f)
        }
        chargeIndicator?.let {
            it.position.x = canonX.toDouble()
            it.position.y = canonY.toDouble()
        }
    }

    fun increaseAngle(p: Int){
        angle += p
        if (angle < 0) angle += 180
        else if (angle > 180) angle -= 180

        onAngleChanged()
    }

    fun increasePower(p: Int){
        power += p
        if (power < 0) power = 0
        else if (power > 1000) power = 1000

        onPowerChanged()
    }

    fun getFireVelocity(power: Int = this.power) :Vec2D {
        val velocity = Vec2D(
            position.copy(),
            Pos2D(this.canonX.toDouble(), this.canonY.toDouble())
        ).times(power / 400.0)
        return velocity
    }

    private var movePressBuffer = 10

    fun onLeftKeyPressed() {
        if (direction == Direction.RIGHT) {
            direction = Direction.LEFT
            increaseAngle((90 - angle).toInt()*2)
            movePressBuffer = 10
        } else if (movePressBuffer > 0) {
            movePressBuffer -= 1
        } else if (position.x > 1 && fuel > 0) {
            // drive left
            AudioHelper.loop(SND_DRIVE)
            fuel -= 0.1
            val newY = rasterTerrain.surfaceAt(position.x.toInt()-1, minY = (position.y - 5).toInt())
            val dy = newY - position.y // positive - tank is moving down
            if (dy > -5 && dy < 10) {
                position.x -= 1
                position.y = newY.toDouble()
                onTankMoved(pokeTerrain = false)
            }
        }
    }

    fun onRightKeyPressed() {
        if (direction == Direction.LEFT) {
            direction = Direction.RIGHT
            increaseAngle(-(angle-90).toInt()*2)
            movePressBuffer = 10
        } else if (movePressBuffer > 0) {
            movePressBuffer -= 1
        } else if (position.x < parent.width-1 && fuel > 0) {
            // drive right
            AudioHelper.loop(SND_DRIVE)
            fuel -= 0.1
            val newY = rasterTerrain.surfaceAt(position.x.toInt()+1, minY = (position.y - 5).toInt())
            val dy = newY - position.y // positive - tank is moving down
            if (dy > -5 && dy < 10) {
                position.x += 1
                position.y = newY.toDouble()
                onTankMoved(pokeTerrain = false)
            }
        }
    }

    fun onLeftOrRightKeyReleased() {
        AudioHelper.stop(SND_DRIVE)
    }

    fun onUpKeyPressed() {
        if (direction == Direction.RIGHT && angle < 90) {
            AudioHelper.loop(SND_CHANGE_ANGLE, -1)
            increaseAngle(1)
        } else if (direction == Direction.LEFT && angle > 90) {
            AudioHelper.loop(SND_CHANGE_ANGLE, -1)
            increaseAngle(-1)
        }
    }

    fun onDownKeyPressed() {
        if (direction == Direction.RIGHT && angle > 0) {
            AudioHelper.loop(SND_CHANGE_ANGLE, -1)
            increaseAngle(-1)
        } else if (direction == Direction.LEFT && angle < 180) {
            AudioHelper.loop(SND_CHANGE_ANGLE, -1)
            increaseAngle(1)
        }
    }

    fun onUpOrDownKeyReleased() {
        AudioHelper.stop(SND_CHANGE_ANGLE)
    }

    fun onSpaceKeyPressed() {
        if (chargeIndicator == null) {
            chargeIndicator = ChargeIndicator(
                parent,
                Pos2D(canonX.toDouble(), canonY.toDouble()),
                this
            ).also {
                parent.add(it)
            }
        }
    }

    fun onSpaceKeyReleased() {
        chargeIndicator?.let { parent.remove(it) }
        chargeIndicator = null
    }
}