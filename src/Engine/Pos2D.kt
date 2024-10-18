package Engine

data class Pos2D(var x: Double, var y: Double){
    fun translate(vec: Vec2D) {
        x += vec.x
        y += vec.y
    }

    operator fun plus(vec: Vec2D) : Pos2D = Pos2D(x + vec.x, y + vec.y)

    fun distance(other: Pos2D): Double = Vec2D(this, other).mag()
}