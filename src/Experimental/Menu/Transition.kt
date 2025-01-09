package Experimental.Menu

import Engine.*
import gameWindow
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp
import kotlin.math.max

/**
 * Transition must be added in init { } of a GameScene, in order for it to
 * look as intended.
 */
class Transition(parent: IGameScene, val length: Double = 0.5) : GameObject2(parent, Pos2D(0.0, 0.0)) {
    var transitionTicksLeft = (GameRunner.fps*length).toInt()

    val image: BufferedImage? =
        gameWindow?.gameRunner?.currentGameScene?.drawOnImage()

    override fun update() {
        transitionTick += 1
        transitionTicksLeft -= 1
        if (transitionTicksLeft == 0) {
            parent.remove(this)
        }
    }

    private val offsets = FloatArray(4)
    private var transitionTick: Float = 0f
    private var transitionAlpha: Float = 1f

    override fun draw(g: Graphics2D) {
        if (image == null) return

        drawAsHud(g) {
            if (transitionTicksLeft > 0) {

                val w = max(0.9f, (transitionTick / (length*10)).toFloat())
                g.drawImage(
                    image,
                    RescaleOp(
                        arrayOf(w, w, w, transitionAlpha).toFloatArray(),
                        offsets,
                        null
                    ),
                    0,
                    0
                )

                transitionAlpha = transitionTicksLeft / (GameRunner.fps*length).toFloat()
            }
        }
    }
}