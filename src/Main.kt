import Experimental.CollisionBalls.Pos2D
import Experimental.Menu.MenuGameScene
import java.awt.*
import java.awt.geom.Point2D
import java.awt.image.VolatileImage
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.times

fun main() {
    println("Hello World!")
    GameWindow(800, 600, "Hersi").apply {
        //currentGameScene = BallGameScene(width, height)
        //currentGameScene = CollisionBallsGameScene(Color.LIGHT_GRAY, width, height)
        currentGameScene = MenuGameScene(width, height, Color.WHITE)
        run()
    }
}

var _id: Int = 1
fun nextId(): Int { return _id++ }

open class GameWindow(val width: Int, val height: Int, title: String) {
    val panel: JPanel = JPanel()
    val frame: JFrame = JFrame()

    init {
        panel.preferredSize = Dimension(width, height)
        panel.size = Dimension(width, height)
        panel.isFocusable = true
        panel.requestFocusInWindow()

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.add(panel)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

    var image: VolatileImage = panel.createVolatileImage(width, height).also {
        if (it == null) { throw Error("Could not create image") }
    }
    lateinit var currentGameScene: IGameScene

    val fps: Double = 60.0

    fun run(){
        val period: Duration = (1 / fps).seconds
        var maxSkips: Int = 5
        var timeTaken: Duration
        while(true){
            var updatesPerDraw = 1
            timeTaken = 0.seconds
            while (updatesPerDraw < maxSkips){
                timeTaken += measureTime {
                    update()
                    if (updatesPerDraw == 1){
                        renderBuffer()
                        renderScreen()
                    }
                }
                if (timeTaken < (updatesPerDraw * period)){
                    if (updatesPerDraw > 1) { println("Skipped ${updatesPerDraw-1} draws") }
                    Thread.sleep(((updatesPerDraw * period) - timeTaken).inWholeMilliseconds)
                    break;
                }
                updatesPerDraw += 1
            }
        }
    }

    fun update(){
        currentGameScene.update()
    }

    fun renderBuffer() {
        if (image.validate(panel.graphicsConfiguration) == VolatileImage.IMAGE_INCOMPATIBLE) {
            image = panel.createVolatileImage(width, height)
        }
        val g: Graphics2D = image.createGraphics()
        currentGameScene.draw(g)
        g.dispose()
    }

    fun renderScreen() {
        panel.graphics.drawImage(image, 0, 0, null)
        Toolkit.getDefaultToolkit().sync()
    }
}

interface IGameScene {
    fun update()
    fun draw(g: Graphics2D)
    fun add(gameObject: IGameObject)
    fun remove(gameObject: IGameObject)
    val width: Int
    val height: Int
}

abstract class GameScene(val color: Color, override val width: Int, override val height:Int) :IGameScene{
    val id = nextId()
    private val gameObjects: MutableMap<Int, IGameObject> = mutableMapOf()
    private val gameObjectsToAdd: MutableMap<Int, IGameObject> = mutableMapOf()
    private val gameObjectsToRemove: MutableSet<Int> = mutableSetOf()

    override fun add(gameObject: IGameObject) { gameObjectsToAdd.put(gameObject.id, gameObject) }
    override fun remove(gameObject: IGameObject) { gameObjectsToRemove.add(gameObject.id) }

    override fun update(){
        gameObjects.forEach {
            it.value.update()
        }

        gameObjectsToRemove.forEach { gameObjects.remove(it) }
        gameObjects.putAll(gameObjectsToAdd)
        if (gameObjectsToAdd.size > 0 || gameObjectsToRemove.size > 0){
            println("GameObjects: ${gameObjects.size}")
        }
        gameObjectsToRemove.clear()
        gameObjectsToAdd.clear()
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillRect(0, 0, width, height)

        gameObjects.forEach {
            it.value.draw(g)
        }
    }
}

interface IGameObject{
    val id: Int
    fun update()
    fun draw(g: Graphics2D)
}

abstract class GameObject(parent: IGameScene, position: Point2D.Float) : IGameObject {
    override val id = nextId()
    override abstract fun update()
    override abstract fun draw(g: Graphics2D)
}

abstract class GameObject2(parent: IGameScene, position: Pos2D) : IGameObject {
    override val id = nextId()
    override abstract fun update()
    override abstract fun draw(g: Graphics2D)
}

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
    return from + Random.nextFloat()*(to-from)
}

fun Random.nextPoint2D(width: Int, height: Int): Point2D.Float {
    return Random.nextPoint2D(width.toFloat(), height.toFloat())
}

fun Random.nextPoint2D(width: Float, height: Float): Point2D.Float {
    return Point2D.Float(Random.nextFloat(0f, width), Random.nextFloat(0f, height))
}

fun Random.nextPos2D(width: Double, height: Double): Pos2D {
    return Pos2D(Random.nextDouble(0.0, width), Random.nextDouble(0.0, height))
}

fun Random.nextColor(): Color {
    return Color(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
}

