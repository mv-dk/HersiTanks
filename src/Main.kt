import java.awt.*
import java.awt.geom.Point2D
import java.awt.image.VolatileImage
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.times

fun main() {
    println("Hello World!")
    GameWindow(800, 600, "Hersi").apply { run() }
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
    var currentGameScene: IGameScene = BallGameScene(width, height)

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
    fun add(gameObject: GameObject)
    fun remove(gameObject: GameObject)
    val width: Int
    val height: Int
}

abstract class GameScene(val color: Color, override val width: Int, override val height:Int) :IGameScene{
    val id = nextId()
    private val gameObjects: MutableMap<Int, GameObject> = mutableMapOf()
    private val gameObjectsToAdd: MutableMap<Int, GameObject> = mutableMapOf()
    private val gameObjectsToRemove: MutableSet<Int> = mutableSetOf()

    override fun add(gameObject: GameObject) { gameObjectsToAdd.put(gameObject.id, gameObject) }
    override fun remove(gameObject: GameObject) { gameObjectsToRemove.add(gameObject.id) }

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

abstract class GameObject(parent: IGameScene, position: Point2D.Float) {
    val id = nextId()
    abstract fun update()
    abstract fun draw(g: Graphics2D)
}

fun Point2D.Float.translate(other: Point2D.Float) {
    this.x += other.x
    this.y += other.y
}

class BallGameObject(
    private val parent: IGameScene,
    var position: Point2D.Float,
    val size: Float,
    val color: Color
): GameObject(parent, position) {
    var velocity = Point2D.Float(0f,0f)
    var life: Int = 10000

    constructor(parent: IGameScene):
            this(parent,
                Point2D.Float(
                    Random.nextInt(0, parent.width).toFloat(),
                    Random.nextInt(0, parent.height).toFloat()),
                Random.nextDouble(10.0, 40.0).toFloat(),
                Color(
                    Random.nextInt(255),
                    Random.nextInt(255),
                    Random.nextInt(255))) {
        velocity = Point2D.Float(
            -10f + 20 * Random.nextFloat(),
            -10f + 20 * Random.nextFloat())
    }


    override fun update() {
        life -= 1
        if (life == 0) {
            parent.remove(this)
            return
        }
        position.translate(velocity)

        velocity.y += 1f
        if (position.x - size/2 < 0) {
            position.x = size/2
            velocity.x *= -1
        }
        else if (position.x + size/2 > parent.width) {
            position.x = parent.width - size/2
            velocity.x *= -1
        }
        if (position.y - size/2 < 0){
            position.y = (size/2) + 1
            velocity.y *= -1
        }
        else if (position.y + size/2 > parent.height){
            position.y = parent.height - size/2
            //velocity.y *= -0.6f
            velocity.y *= -1
            if (Math.abs(velocity.y) <= 3) velocity.y = 0f
            velocity.x *= 0.8f
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillOval((position.x - size/2).toInt(), (position.y - size/2).toInt(), size.toInt(), size.toInt())
    }

}

class BallGameScene(windowWidth: Int, windowHeight: Int) :GameScene(Color.LIGHT_GRAY, windowWidth, windowHeight) {
    var ballsToCreate = 10000

    override fun update() {
        super.update()

        if (ballsToCreate > 0){
            add(BallGameObject(this))
            ballsToCreate -= 1
        }
    }
}