package Experimental.TerrainScene

import Engine.*
import Game.BattleState
import Game.GameController
import Game.Team
import gameWindow
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import kotlin.random.Random

val random = Random(1)

class TerrainGameScene(private val parentScene: IGameScene, color: Color, width: Int, height: Int) : GameScene(color, width, height) {
    lateinit var rasterTerrain: RasterTerrain
    var updatePlayersTurnOnNextUpdate = false

    override fun load() {
        GameController.state = BattleState()
        rasterTerrain = RasterTerrain(this, Pos2D(0.0, 0.0))
        add(rasterTerrain)

        val margin = 40.0
        var numPlayers = 2
        try {
            numPlayers = GameController.settings["Players"] as Int
        } catch (e: Exception) { }
        val colors = listOf(Color.RED, Color.BLUE, Color.CYAN, Color.YELLOW, Color.BLACK, Color.WHITE, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.LIGHT_GRAY)
        val spaceBetweenTanks = if (numPlayers == 1) (width-margin)/2.0 else ((width-2.0*margin) / (numPlayers-1))
        var x = margin
        for (i in 1 .. numPlayers){
            val tank = Tank(this, rasterTerrain, Pos2D(x, 30.0), colors[i-1])
            tank.falling = true
            GameController.addTeam(Team("Tank$i", mutableListOf(tank)))
            add(tank)
            x += spaceBetweenTanks
        }
    }

    fun acceptInput(): Boolean{
        if (rasterTerrain.crumble || rasterTerrain.earthquake != null) return false
        if (GameController.tanks.any {it.alive && it.falling}) return false
        if (GameController.projectilesFlying > 0) return false
        if (GameController.explosionsActive > 0) return false
        return true
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_ESCAPE){
            GameController.onGoingToMenu()
            gameWindow?.gameRunner?.currentGameScene = parentScene
        }

        if (!acceptInput()) return

        if (e.keyCode == KeyEvent.VK_1) {
            rasterTerrain.mode = 1
        } else if (e.keyCode == KeyEvent.VK_2){
            rasterTerrain.mode = 2
        } else if (e.keyCode == KeyEvent.VK_3){
            rasterTerrain.mode = 3
        } else if (e.keyCode == KeyEvent.VK_LEFT) {
            GameController.getCurrentTank().increaseAngle(1)
        } else if (e.keyCode == KeyEvent.VK_RIGHT){
            GameController.getCurrentTank().increaseAngle(-1)
        } else if (e.keyCode == KeyEvent.VK_DOWN) {
            GameController.getCurrentTank().increasePower(-1)
        } else if (e.keyCode == KeyEvent.VK_UP) {
            GameController.getCurrentTank().increasePower(1)
        }else if (e.keyCode == KeyEvent.VK_ENTER){
            AudioHelper.play("fire")
            val tank = GameController.getCurrentTank()
            val projectile = Projectile(this, tank.position.copy(),
                Vec2D(tank.position.copy(), Pos2D(tank.canonX.toDouble(), tank.canonY.toDouble())).times(tank.power/100.0))
            add(projectile)
            updatePlayersTurnOnNextUpdate = true
        }
    }

    override fun mousePressed(e: MouseEvent) {
        if (!acceptInput()) return
        if (e.button == MouseEvent.BUTTON1) {
            rasterTerrain.mouseClicked(e.x, e.y)
        } else if (e.button == MouseEvent.BUTTON3){
            AudioHelper.play("big-boom")
        }
    }

    override fun update() {
        if (updatePlayersTurnOnNextUpdate){
            updatePlayersTurnOnNextUpdate = false
            GameController.nextPlayersTurn()
        }
        super.update()
    }
}

class RasterTerrain(val parent: IGameScene, position: Pos2D) : GameObject2(parent, position){
    var rasterImage: BufferedImage = BufferedImage(parent.width, parent.height, BufferedImage.BITMASK)
    var mode: Int = 1
    var crumble: Boolean = false
    var earthquake: Earthquake? = null

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
                    if (rasterImage.getRGB(x, y) == 0 && rasterImage.getRGB(x, y-10) != 0){
                        rasterImage.setRGB(x,y, rasterImage.getRGB(x, y-10))
                        rasterImage.setRGB(x,y-10, 0)
                        crumbleCounter += 1
                    } else if (rasterImage.getRGB(x, y) == 0 && rasterImage.getRGB(x, y-5) != 0){
                        rasterImage.setRGB(x,y, rasterImage.getRGB(x, y-5))
                        rasterImage.setRGB(x,y-5, 0)
                        crumbleCounter += 1
                    } else if (rasterImage.getRGB(x, y) == 0 && rasterImage.getRGB(x, y-2) != 0){
                        rasterImage.setRGB(x,y, rasterImage.getRGB(x, y-2))
                        rasterImage.setRGB(x,y-2, 0)
                        crumbleCounter += 1
                    } else if (rasterImage.getRGB(x, y) == 0 && rasterImage.getRGB(x, y-1) != 0){
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
                for (t in GameController.tanks.filter { it.alive }) {
                    t.falling = true
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
        gg.color = Color(0,0,0,0)
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
        earthquake = Earthquake(x,y, 5, 100, 1.0)

        AudioHelper.loop("earthquake", -1)
    }

    override fun draw(g: Graphics2D) {
        g.drawImage(rasterImage, null, 0, 0);
    }
}

class Earthquake(val x: Int, val y: Int, numCracks: Int, val size: Int, val branchProbability: Double){
    var cracks = mutableListOf<EarthquakeCrack>()

    init {
        for (i in 0..numCracks-1){
            cracks.add(createCrack(x.toDouble(), y.toDouble(), size))
        }
    }

    fun createCrack(x: Double, y: Double, growDuration: Int): EarthquakeCrack {
        return EarthquakeCrack(
            x,
            y,
            Vec2D(random.nextDouble(-5.0, 5.0), random.nextDouble(-5.0, 5.0)),
            growDuration)
    }

    fun update(terrain: RasterTerrain): Boolean {
        var didChangeTerrain = false
        val newCracks = mutableListOf<EarthquakeCrack>()
        for (crack in cracks){
            if (crack.growDuration > 0) {
                crack.growDuration -= 1

                val holeSize = Math.min(4, crack.growDuration/10)
                val oldX = crack.x
                val oldY = crack.y

                if (random.nextDouble() < 0.1 || crack.growDuration % 6 == 0){
                    crack.x -= crack.direction.y
                    crack.y += crack.direction.x
                } else {
                    crack.x += crack.direction.x
                    crack.y += crack.direction.y
                    val futureX = crack.x + crack.direction.x * 10.0
                    val futureY = crack.y + crack.direction.y * 10.0
                    if (futureX < 0 || futureX > terrain.rasterImage.width || futureY < 0 || futureY > terrain.rasterImage.height ||
                        terrain.rasterImage.getRGB(futureX.toInt(), futureY.toInt()) == 0){
                        crack.growDuration = 0
                        continue
                    }
                }
                terrain.pokeLine(oldX.toInt(), oldY.toInt(), crack.x.toInt(), crack.y.toInt(), holeSize.toFloat())
                if (random.nextDouble() < 0.9) {
                    terrain.pokeLine(
                        oldX.toInt(),
                        oldY.toInt(),
                        crack.x.toInt() - 10 + random.nextInt(20),
                        crack.y.toInt() - 10 + random.nextInt(20),
                        2f
                    )
                }
                if (random.nextDouble() < branchProbability/30){
                    if (random.nextDouble() < 0.1){
                        newCracks.add(createCrack(crack.x, crack.y, size))
                    } else {
                        newCracks.add(createCrack(crack.x, crack.y, (crack.growDuration * 1.0).toInt()))
                    }
                }

                crack.direction.x += random.nextDouble(-0.10, 0.10)
                crack.direction.y += random.nextDouble(-0.10, 0.10)
                didChangeTerrain = true
            }
        }
        cracks.addAll(newCracks)

        return didChangeTerrain
    }

    fun remove(){
        cracks.clear()
    }
}

class EarthquakeCrack(var x: Double, var y: Double, val direction: Vec2D, var growDuration: Int) {

}