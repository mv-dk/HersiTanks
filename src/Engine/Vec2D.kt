package Engine

import kotlin.math.acos
import kotlin.math.sqrt

data class Vec2D(var x: Double, var y: Double) {
    constructor(p1: Pos2D, p2: Pos2D) : this(p2.x - p1.x, p2.y - p1.y)

    fun copy(): Vec2D = Vec2D(x, y)
    operator fun plus(other: Vec2D): Vec2D = Vec2D(x + other.x, y + other.y)
    fun unaryMinus(): Vec2D { return Vec2D(-x, -y)
    }
    operator fun minus(other: Vec2D) : Vec2D = Vec2D(x - other.x, y - other.y)
    fun mag(): Double = sqrt(x * x + y * y)
    fun dot(other: Vec2D) : Double = x * other.x + y * other.y
    fun angleTo(other: Vec2D) : Double = acos(this.dot(other) / (mag() * other.mag()))
    fun hat(): Vec2D = Vec2D(-y, x)
    operator fun times(num: Double): Vec2D = Vec2D(x * num, y * num)
    operator fun div(num: Double): Vec2D = Vec2D(x / num, y / num)
    fun normalized(): Vec2D = Vec2D(x, y) /mag()
    fun reflect(other: Vec2D): Vec2D {
        val n = other.normalized().hat()
        return this - n * (2*(this.dot(n)))
    }
}