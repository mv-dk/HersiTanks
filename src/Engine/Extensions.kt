package Engine

import java.awt.Color
import kotlin.random.Random
import java.awt.geom.Point2D
import kotlin.math.max
import kotlin.math.min
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
    return Color(min(this.red+degree, 255), min(this.green+degree, 255), min(this.blue+degree, 255))
}

fun Color.redder(degree: Int): Color {
    return Color(min(this.red + degree, 255), this.green, this.blue)
}

fun Color.greener(degree: Int): Color {
    return Color(this.red, min(this.green+degree, 255), this.blue)
}

fun Color.bluer(degree: Int): Color {
    return Color(this.red, this.green, min(this.blue+degree, 255))
}

fun Color.darker(degree: Int): Color {
    return Color(max(this.red-degree, 0), max(this.green-degree, 0), max(this.blue-degree, 0))
}

fun Color.mult(factor: Double): Color {
    return Color(min(255, (this.red*factor).toInt()), min(255, (this.green*factor).toInt()), min(255, (this.blue*factor).toInt()))
}

fun Color.contrast(factor: Double = 2.0): Color {
    return Color(
        min(255, max(0, ((this.red - 128) * factor).toInt() + 128)),
        min(255, max(0, ((this.green - 128) * factor).toInt() + 128)),
        min(255, max(0, ((this.blue - 128) * factor).toInt() + 128))
    )
}