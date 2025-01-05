package Experimental.TerrainScene

import Engine.*
import gameResX
import gameResY
import kotlin.time.times

class Viewport(
    private val gameScene: GameScene,
    val width: Int,
    val height: Int,
    var x: Int,
    var y: Int,
    var minX: Int = Int.MIN_VALUE,
    var maxX: Int = Int.MAX_VALUE,
    var minY: Int = Int.MIN_VALUE,
    var maxY: Int = Int.MAX_VALUE) {

    var ticksLeft = 0
    var target: Pos2D = Pos2D(0.0, 0.0)

    fun setFocus(x: Double, y: Double) {
        target.x = x
        target.y = y
        ticksLeft = (0.5 * (GameRunner.fps)).toInt()
    }

    fun setFocus(pos: Pos2D) { setFocus(pos.x, pos.y)}

    fun inside(x: Double, y: Double): Boolean {
        if (x < this.x) return false
        if (y < this.y) return false
        if (x > this.x + this.width) return false
        if (y > this.y + this.height) return false
        return true
    }

    fun inside(gameObject: GameObject2): Boolean {
        if (gameObject is Tank) {
            if (inside(gameObject.position.x - gameObject.size, gameObject.position.y - gameObject.size)) return true
            if (inside(gameObject.position.x - gameObject.size, gameObject.position.y + gameObject.size)) return true
            if (inside(gameObject.position.x + gameObject.size, gameObject.position.y - gameObject.size)) return true
            if (inside(gameObject.position.x + gameObject.size, gameObject.position.y + gameObject.size)) return true
            return false
        }
        if (inside(gameObject.position.x, gameObject.position.y)) return true
        return false
    }

    fun update() {
        if (ticksLeft > 0) {
            this.x = Math.min(maxX, (this.x * 0.9 + 0.1 * (target.x - gameResX/2)).toInt())
            if (this.x < minX) this.x = minX

            this.y = Math.min(maxY, (this.y * 0.9 + 0.1 * (target.y - gameResY/2)).toInt())
            if (this.y < minY) this.y = minY

            if (inside(target.x, target.y)) {
                ticksLeft = (ticksLeft*0.8).toInt()
            } else {
                ticksLeft -= 1
            }
        }
    }
}