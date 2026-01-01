package Game.Menu

import Engine.*
import Game.Helpers.FontHelper
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import kotlin.random.Random

private val rand = Random(1)

class HersiTanksTextGameObject(
    parent: IGameScene,
    position: Pos2D,
    var size: Float = 1f
): GameObject2(parent, position) {
    private var font: Font? = null
    private var biggerFont: Font? = null
    private var initialized = false

    override fun onAdded() {
        super.onAdded()
        font = FontHelper.balooFont?.deriveFont(Font.PLAIN, 30f * size)
        biggerFont = FontHelper.balooFont?.deriveFont(Font.PLAIN, 33f * size)
    }

    private fun initialize() {
        val fp = 5
        val fz = 0.5f
        parent.add(Letter(parent,'H', position.copy(), font,biggerFont, 90, fp, fz))
        parent.add(Letter(parent,'e', position.copy(x = position.x + 20*size), font,biggerFont, 80, fp, fz))
        parent.add(Letter(parent,'r', position.copy(x = position.x + 37*size), font,biggerFont, 70, fp, fz))
        parent.add(Letter(parent,'s', position.copy(x = position.x + 50*size), font,biggerFont, 60, fp, fz))
        parent.add(Letter(parent,'i', position.copy(x = position.x + 65*size), font,biggerFont, 50, fp, fz))


        parent.add(Letter(parent,'T', position.copy(y = position.y + 30 * size), font,biggerFont, 40, fp, fz))
        parent.add(Letter(parent,'a', position.copy(x = position.x + 16*size, y = position.y + 30 * size), font,biggerFont, 30, fp, fz))
        parent.add(Letter(parent,'n', position.copy(x = position.x + 32*size, y = position.y + 30 * size), font,biggerFont, 20, fp, fz))
        parent.add(Letter(parent,'k', position.copy(x = position.x + 49*size, y = position.y + 30 * size), font,biggerFont, 10, fp, fz))
        parent.add(Letter(parent,'s', position.copy(x = position.x + 65*size, y = position.y + 30 * size), font,biggerFont, 0, fp, fz))

        initialized = true
    }

    override fun update() {
        if (!initialized) initialize()

    }

    override fun draw(g: Graphics2D) {

    }
}

class Letter(
    parent: IGameScene,
    val character: Char,
    position: Pos2D,
    val font: Font?,
    val biggerFont: Font?,
    var timeOffset: Int = 0,
    var flashPeriod: Int = 1,
    var freq: Float = 1f
): GameObject2(parent, position)  {
    private var i: Int = timeOffset
    private val color = (parent as GameScene).color
    private var flashColor = Color(40, 200, 80)
    private val goalPosition = position.copy()
    private var initialized = false
    private var velocity: Vec2D = Vec2D(rand.nextInt(0, 5), rand.nextInt(-5, 5))

    private fun initialize() {
        position.x = 0.0
        position.y = rand.nextDouble(parent.height.toDouble())

        initialized = true
    }

    private fun onFlash() {

    }

    override fun update() {
        if (!initialized) initialize()

        i += 1
        if (i >= GameRunner.fps / freq) {
            i = 0
            onFlash()
        }
        var diff = Vec2D(position, goalPosition)
        val mag = diff.mag()
        if (mag == 0.0) return

        if (mag > 1) diff = diff.normalized()
        if (mag > 0.2)
            velocity = velocity.plus(diff)
        else
            velocity = diff
        velocity = velocity.times(0.95)
        position.x += velocity.x
        position.y += velocity.y
    }

    private fun drawFlash(g: Graphics2D) {
        g.font = biggerFont
        g.color = flashColor
    }

    private fun drawRegular(g: Graphics2D) {
        g.font = font
        g.color = color
    }

    override fun draw(g: Graphics2D) {
        if (g.font == null) return

        if (i <= flashPeriod) {
            drawFlash(g)
        } else {
            drawRegular(g)
        }
        FontHelper.drawStringWithShadow(g, character.toString(), position.x.toInt(), position.y.toInt())
    }

}