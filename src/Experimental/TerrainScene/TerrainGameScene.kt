package Experimental.TerrainScene

import Engine.*
import gameWindow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ColorModel
import kotlin.math.sin
import kotlin.random.Random

class TerrainGameScene(private val parentScene: IGameScene, color: Color, width: Int, height: Int) : GameScene(color, width, height) {
    override fun load() {
        add(RasterTerrain(this, Pos2D(0.0, 0.0)))
    }

    override fun keyTyped(e: KeyEvent?) = Unit

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_ESCAPE){
            gameWindow?.currentGameScene = parentScene
        }
    }

    override fun keyReleased(e: KeyEvent?) = Unit
}

class RasterTerrain(parent: IGameScene, position: Pos2D) : GameObject2(parent, position){
    var rasterImage: BufferedImage = BufferedImage(parent.width, parent.height, ColorModel.BITMASK)

    init {
        val g = rasterImage.createGraphics()
        g.color = Color.GREEN;

        val rand = Random(System.currentTimeMillis())
        val xs = mutableListOf<Int>()
        var ys = mutableListOf<Int>()
        var xstep = rasterImage.width/10;
        var ystep = 10
        var tmpx = 0
        var tmpy = rasterImage.height/2
        xs.add(tmpx)
        ys.add(tmpy)

        var i = 0
        while (i < 10){
            tmpx += xstep
            tmpy += if (rand.nextDouble() < 0.5) ystep else -ystep
            ystep = (50.0 * rand.nextDouble()).toInt()
            xs.add(tmpx)
            ys.add(tmpy)
            i += 1
        }

        xs.add(rasterImage.width)
        ys.add(tmpy)

        xs.add(rasterImage.width)
        ys.add(rasterImage.height)

        xs.add(0)
        ys.add(rasterImage.height)

        xs.add(0)
        ys.add(rasterImage.height/2)

        g.fillPolygon(xs.toIntArray(), ys.toIntArray(), xs.size)
    }

    override fun update() {

    }

    override fun draw(g: Graphics2D) {
        g.drawImage(rasterImage, null, 0, 0);
    }
}