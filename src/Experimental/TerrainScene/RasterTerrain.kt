package Experimental.TerrainScene

import Engine.*
import Game.GameController
import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.random.Random

class RasterTerrain(parent: IGameScene, position: Pos2D) : GameObject2(parent, position){
    var rasterImage: BufferedImage = BufferedImage(parent.width, parent.height, BufferedImage.BITMASK)
    var mode: Int = 1
    var crumble: Boolean = false
    var earthquake: Earthquake? = null

    init {
        val g = rasterImage.createGraphics()
        g.color = Color.GREEN.darker(40);

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
            if (tmpy < 120) {
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

        addColoredTopLayer(rasterImage, 10, Color(0, 220, 0))
        addColoredTopLayer(rasterImage, 8, Color(0, 200, 0))
        addColoredTopLayer(rasterImage, 6, Color(0, 180, 0))
        addColoredTopLayer(rasterImage, 4, Color(0, 160, 0))
        addColoredTopLayer(rasterImage, 2, Color(0, 140, 0))

    }

    fun addColoredTopLayer(rasterImage: BufferedImage, depth: Int, color: Color){
        for (y in depth .. rasterImage.height - 1) {
            for (x in 0 .. rasterImage.width - 1) {
                if (rasterImage.getRGB(x,y) != 0){
                    if (rasterImage.getRGB(x, y-depth) == 0){
                        rasterImage.setRGB(x,y ,color.rgb)
                    }
                }
            }
        }
    }

    override fun update() {
        if (crumble){
            var crumbleCounter = 0
            for (yFromTop in 1..rasterImage.height - 10) {
                val y = rasterImage.height - yFromTop
                for (x in 0..rasterImage.width-1) {
                    if (rasterImage.getRGB(x, y) == 0 && rasterImage.getRGB(x, y-1) != 0){
                        rasterImage.setRGB(x,y, rasterImage.getRGB(x, y-1))
                        rasterImage.setRGB(x,y-1, 0)
                        crumbleCounter += 1
                    } else if (x > 0 && x < rasterImage.width-1){
                        // Remove columns of 1 px width
                        if (y < rasterImage.height-1 && y > 0 &&
                            rasterImage.getRGB(x, y) != 0 && rasterImage.getRGB(x, y+1) != 0 &&
                            rasterImage.getRGB(x-1, y) == 0 && rasterImage.getRGB(x+1, y) == 0 &&
                            rasterImage.getRGB(x-1, y+1) == 0 && rasterImage.getRGB(x+1, y+1) == 0) {
                            rasterImage.setRGB(x, y, 0)
                            crumbleCounter += 1
                        }
                    }
                }
            }
            if (crumbleCounter == 0) {
                crumble = false
                for (t in GameController.players.filter { it.playing }) {
                    t.tank?.falling = true
                }
            }
        } else if (earthquake != null){
            val didUpdateTerrain: Boolean = earthquake?.update(this) == true
            if (!didUpdateTerrain) {
                earthquake?.remove()
                earthquake = null
                AudioHelper.stop("earthquake")
                maybeStartCrumble()
            }
        }
    }

    fun mouseClicked(x: Int, y: Int) {
        println("Mouse was clicked at $x,$y")
        if (mode == 1) {
            pokeHole(x,y);
            maybeStartCrumble()
        } else if (mode == 2){
            startCrumble();
        } else if (mode == 3){
            startEarthquake(x,y)
        }
    }

    fun pokeHole(x: Int, y: Int, size: Int = 60){
        val gg = rasterImage.createGraphics()
        gg.color = Color(0, 0, 0, 0)
        gg.composite = AlphaComposite.Clear
        gg.fillOval(x - size / 2, y - size / 2, size, size)
        AudioHelper.play("small-boom")
    }

    fun pokeLine(x1: Int, y1: Int, x2: Int, y2: Int, width: Float){
        val gg = rasterImage.createGraphics()
        gg.color = Color(0, 0, 0, 0)
        gg.composite = AlphaComposite.Clear
        gg.stroke = BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gg.drawLine(x1, y1, x2, y2)
    }

    private fun maybeStartCrumble(){
        if (random.nextDouble() <= 0.5) startCrumble()
    }

    private fun startCrumble(){
        crumble = true

    }

    fun startEarthquake(x: Int, y:Int){
        earthquake = Earthquake(x, y, 5, 100, 1.0)

        AudioHelper.loop("earthquake", -1)
    }

    override fun draw(g: Graphics2D) {
        g.drawImage(rasterImage, null, 0, 0);
    }
}