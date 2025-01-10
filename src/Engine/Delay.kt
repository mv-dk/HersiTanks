package Engine

import gameWindow
import java.awt.Graphics2D

class DelayedAction(
    seconds: Double,
    val action: () -> Unit
    ): GameObject2(gameWindow!!.gameRunner.currentGameScene, Pos2D(0.0, 0.0)) {
    var ticks = (seconds * GameRunner.fps).toInt()

    init {
        parent.add(this)
    }

    override fun update() {
        ticks -= 1
        if (ticks == 0) {
            parent.remove(this)
            action()
        }
    }

    override fun draw(g: Graphics2D) = Unit
}