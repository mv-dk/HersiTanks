package Game.TerrainScene

import Engine.*
import Game.Menu.OPTION_GROUND_GRASS
import Game.Menu.OPTION_GROUND_SNOW
import Game.GameController
import SND_EARTHQUAKE
import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class RasterTerrain(parent: IGameScene, position: Pos2D, width: Int, height: Int) : GameObject2(parent, position){
    var rasterImage: BufferedImage = BufferedImage(width, height, BufferedImage.BITMASK)
    var mode: Int = 1
    var crumble: Boolean = false
    var earthquake: Earthquake? = null
    lateinit var primaryColor: Color
    lateinit var darkerColor: Color
    lateinit var darkOutlineColor: Color

    init {
        val g = rasterImage.createGraphics()
        when (GameController.groundOption) {
            OPTION_GROUND_GRASS -> {
                primaryColor = Color.GREEN.darker(40)
                darkerColor = primaryColor.darker(20)
                darkOutlineColor = primaryColor.darker(100)
            }
            OPTION_GROUND_SNOW -> {
                primaryColor = Color(240, 240, 255)
                darkerColor = Color(190, 210, 255)
                darkOutlineColor = Color(150, 190, 255)
            }
            else -> {
                primaryColor = Color.orange.darker(50)
                darkerColor = primaryColor.darker(20)
                darkOutlineColor = primaryColor.darker(100)
            }
        }
        g.color = primaryColor
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

        addOutlines()
        when (GameController.groundOption) {
            OPTION_GROUND_SNOW -> {
                addSnowmen(rasterImage, 5)
            }
        }
    }

    fun addSnowmen(rasterImage: BufferedImage, amount: Int) {
        var x = ((rasterImage.width / amount) + 20 * Math.random()*10).toInt()
        for (i in 1 .. amount) {
            x = ((x + (rasterImage.width / amount) + 20 * Math.random()*10) % rasterImage.width).toInt()


            var y = 0
            while (rasterImage.getRGB(x,y) == 0 && y < rasterImage.height) {
                y += 1
            }

            addSnowman(rasterImage.graphics as Graphics2D, x, y, Random.nextDouble(8.0, 14.0))
        }
    }

    fun addSnowman(g: Graphics2D, x: Int, y: Int, size: Double) {
        addSnowball(g, x, y, size)
        addSnowball(g, x, (y-size*0.7).toInt(), size*0.75)
        addCarrot(g, (x-size*0.1).toInt(), (y-size*1.2).toInt(), size*0.25)
    }

    fun addSnowball(g: Graphics2D, x: Int, y: Int, size: Double) {
        var tx = x
        var ty = y
        g.color = Color(180, 180, 255)
        g.fillOval((tx - size/2.0).toInt(), (ty - size).toInt(), size.toInt(), size.toInt())
        g.color = Color(220, 220, 255)
        g.fillOval((tx - size/2.0).toInt(), (ty - size).toInt(), (size*.8).toInt(), (size*.8).toInt())
    }

    fun addCarrot(g: Graphics2D, x: Int, y: Int, size: Double) {
        g.color = Color(230, 130, 0)
        g.stroke = BasicStroke((size/5.0).toFloat(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g.drawLine(x, y, (x-size).toInt(), (y+size/10.0).toInt())
        g.drawLine(x, (y+size/5.0).toInt(), (x-size).toInt(), (y+size/10.0).toInt())
    }

    fun addColoredTopLayer(rasterImage: BufferedImage, depth: Int, color: Color, startX: Int = 0, endX: Int = rasterImage.width-1){
        for (y in depth .. rasterImage.height - 1) {
            for (x in max(0, startX) .. min(endX, rasterImage.width - 1)) {
                if (rasterImage.getRGB(x,y) != 0){
                    if (rasterImage.getRGB(x, y-depth) == 0){
                        rasterImage.setRGB(x,y ,color.rgb)
                    }
                }
            }
        }
    }

    fun addOutlines(startX: Int = 0, endX: Int = rasterImage.width-1) {
        when (GameController.groundOption) {
            OPTION_GROUND_GRASS -> {
                addColoredTopLayer(rasterImage, 10, darkerColor.darker(20), startX, endX)
                addColoredTopLayer(rasterImage, 2, darkOutlineColor.darker(100), startX, endX)
            }
            OPTION_GROUND_SNOW -> {
                addColoredTopLayer(rasterImage, 10, Color(230,230,255), startX, endX)
                addColoredTopLayer(rasterImage, 8, Color(210, 220, 255), startX, endX)
                addColoredTopLayer(rasterImage, 6, Color(190, 210, 255), startX, endX)
                addColoredTopLayer(rasterImage, 4, Color(170, 200, 255), startX, endX)
                addColoredTopLayer(rasterImage, 2, Color(150, 190, 255), startX, endX)
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
                AudioHelper.stop(SND_EARTHQUAKE)
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

        AudioHelper.loop(SND_EARTHQUAKE, -1)
    }

    override fun draw(g: Graphics2D) {
        g.drawImage(rasterImage, null, 0, 0);
    }

    fun surfaceAt(x: Int): Int {
        var interval = rasterImage.height/2
        var y = interval
        while (interval > 1) {
            interval /= 2
            if (rasterImage.getRGB(x, y) == 0) {
                y += interval
            } else {
                y -= interval
            }
        }
        return y
    }
}