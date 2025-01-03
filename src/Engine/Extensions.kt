package Engine

import java.awt.Color
import kotlin.random.Random
import java.awt.geom.Point2D
import kotlin.math.sqrt
import kotlin.math.pow

fun Point2D.Float.translate(other: Point2D.Float) {
    this.x += other.x
    this.y += other.y
}

fun Point2D.Float.distance(other: Point2D.Float): Float {
    return sqrt((this.x.toDouble() - other.x.toDouble()).pow(2.0) + (this.y.toDouble() - other.y.toDouble()).pow(
        2.0
    )
    ).toFloat()
}

fun Random.nextFloat(from: Float, to: Float): Float{
    return from + this.nextFloat()*(to-from)
}

fun Random.nextPoint2D(width: Int, height: Int): Point2D.Float {
    return this.nextPoint2D(width.toFloat(), height.toFloat())
}

fun Random.nextPoint2D(width: Float, height: Float): Point2D.Float {
    return Point2D.Float(this.nextFloat(0f, width), this.nextFloat(0f, height))
}

fun Random.nextPos2D(width: Double, height: Double): Pos2D {
    return Pos2D(this.nextDouble(0.0, width), this.nextDouble(0.0, height))
}

fun Random.nextColor(): Color {
    return Color(this.nextInt(255), this.nextInt(255), this.nextInt(255))
}

fun Color.lighter(degree: Int): Color {
    return Color(Math.min(this.red+degree, 255), Math.min(this.green+degree, 255), Math.min(this.blue+degree, 255))
}

fun Color.darker(degree: Int): Color {
    return Color(Math.max(this.red-degree, 0), Math.max(this.green-degree, 0), Math.max(this.blue-degree, 0))
}

fun Color.mult(factor: Double): Color {
    return Color((this.red*factor).toInt(), (this.green*factor).toInt(), (this.blue*factor).toInt())
}