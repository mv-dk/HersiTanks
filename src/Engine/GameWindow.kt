package Engine

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.VolatileImage
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.times

/**
 * Creates a JFrame and a JPanel.
 * Contains the game loop in the function run().
 * Contains currentGameScene, on which update() and draw() is called.
 */
open class GameWindow(val width: Int, val height: Int, title: String, gameScene: IGameScene) : Runnable {
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
        gameScene.load()
        panel.addKeyListener(gameScene)
    }

    var image: VolatileImage = panel.createVolatileImage(width, height).also {
        if (it == null) { throw Error("Could not create image") }
    }
    var currentGameScene: IGameScene = gameScene
        get() {
            return field
        }
        set(value) {
            currentGameScene.unload()
            frame.removeKeyListener(currentGameScene)
            field = value
            field.load()
            frame.addKeyListener(field)
        }

    val fps: Double = 60.0

    override fun run(){
        val period: Duration = (1 / fps).seconds
        var maxSkips: Int = 5
        var timeTaken: Duration
        while(true){
            var updatesPerDraw = 1
            timeTaken = 0.seconds
            while (updatesPerDraw < maxSkips){
                timeTaken += measureTime {
                    update()
                    if (updatesPerDraw == 1) {
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