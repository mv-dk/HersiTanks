package Experimental.TerrainScene

import Engine.GameObject2

class Viewport(
    val width: Int,
    val height: Int,
    var x: Int,
    var y: Int) {

    fun inside(x: Int, y: Int): Boolean {
        if (x < this.x) return false
        if (y < this.y) return false
        if (x > this.x + this.width) return false
        if (y > this.y + this.height) return false

        return true
    }

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
}