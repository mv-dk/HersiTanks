package Experimental.TerrainScene

import Engine.*
import gameWindow
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ColorModel
import kotlin.math.sin
import kotlin.random.Random

class TerrainGameScene(private val parentScene: IGameScene, color: Color, width: Int, height: Int) : GameScene(color, width, height) {
    lateinit var rasterTerrain: RasterTerrain


    override fun load() {
        rasterTerrain = RasterTerrain(this, Pos2D(0.0, 0.0))
        add(rasterTerrain)
    }

    override fun keyTyped(e: KeyEvent?) = Unit

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_ESCAPE){
            gameWindow?.gameRunner?.currentGameScene = parentScene
        }
    }

    override fun keyReleased(e: KeyEvent?) = Unit

    override fun mousePressed(e: MouseEvent?) {
        if (e?.button == MouseEvent.BUTTON1) {
            rasterTerrain.mouseClicked(e.x, e.y)
        }
    }
}

class RasterTerrain(parent: IGameScene, position: Pos2D) : GameObject2(parent, position){
    var rasterImage: BufferedImage = BufferedImage(parent.width, parent.height, BufferedImage.BITMASK)

    init {
        val g = rasterImage.createGraphics()
        g.color = Color.GREEN;

        val rand = Random(System.currentTimeMillis())
        val xs = mutableListOf<Int>()
        var ys = mutableListOf<Int>()
        var less_bumpy = 10
        var more_bumpy = 100
        var steps = rand.nextInt(less_bumpy, more_bumpy)
        var xstep = rasterImage.width/steps;
        var ystep = rand.nextInt(-10, 10)
        var tmpx = 0
        var tmpy = rand.nextInt(60, rasterImage.height - 60)
        xs.add(tmpx)
        ys.add(tmpy)

        var i = 0
        while (i < steps){
            tmpx += xstep

            tmpy += ystep
            if (tmpy < 60) {
                ystep = 3
            } else if (tmpy > rasterImage.height - 60) {
                ystep = -3;
            } else {
                ystep += (-steps/10.0 + steps/5.0 * rand.nextDouble()).toInt()
            }

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

    fun mouseClicked(x: Int, y: Int) {
        println("Mouse was clicked at $x,$y")
        val gg = rasterImage.createGraphics()
        gg.color = Color(0, 0, 0, 0)
        val size = 60
        gg.composite = AlphaComposite.Clear
        gg.fillOval(x - size/2, y - size/2 ,size, size)
    }

    override fun draw(g: Graphics2D) {
        g.drawImage(rasterImage, null, 0, 0);
    }
}