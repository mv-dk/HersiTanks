package Engine

data class Pos2D(var x: Double, var y: Double){
    fun translate(vec: Vec2D) {
        x += vec.x
        y += vec.y
    }

    operator fun plus(vec: Vec2D) : Pos2D = Pos2D(x + vec.x, y + vec.y)

    fun distance(other: Pos2D): Double = Vec2D(this, other).mag()
    fun stepsTo(other: Pos2D, steps: Int): List<Pos2D> {
        val result = mutableListOf<Pos2D>()
        val dx = (other.x - this.x)/steps
        val dy = (other.y - this.y)/steps
        for (i in 1 .. steps) {
            result.add(Pos2D(this.x + i * dx, this.y + i * dy))
        }
        return result
    }
}