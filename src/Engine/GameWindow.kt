package Engine

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.RenderingHints
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

interface IGameWindow {
    fun onGameSceneChanged(oldGameScene: IGameScene, newGameScene: IGameScene)
    fun getGraphics2D(): Graphics2D
    fun render()
}

/**
 * Creates a JFrame and a JPanel.
 * Contains the game loop in the function run().
 * Contains currentGameScene, on which update() and draw() is called.
 */
open class GameWindow(val width: Int, val height: Int, title: String, val gameScene: IGameScene) : Runnable, IGameWindow {
    val panel: JPanel = JPanel()
    val frame: JFrame = JFrame()

    val renderingHints = java.util.Map.of(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    var gameRunner: GameRunner = GameRunner(this, gameScene)

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

        panel.addKeyListener(gameScene)
    }

    var image: VolatileImage = panel.createVolatileImage(width, height)

    private fun isImageValid(): Boolean {
        return image.validate(panel.graphicsConfiguration) == VolatileImage.IMAGE_INCOMPATIBLE;
    }

    private fun createNewImage() {
        image = panel.createVolatileImage(width, height)
    }

    private fun createGraphics():Graphics2D {
        val g = image.createGraphics()
        g.setRenderingHints(renderingHints)
        return g;
    }

    override fun render(){
        panel.graphics.drawImage(image, 0, 0, null)
        Toolkit.getDefaultToolkit().sync()
    }

    override fun run() {
        gameRunner.run()
    }

    override fun onGameSceneChanged(oldGameScene: IGameScene, newGameScene: IGameScene) {
        frame.removeKeyListener(oldGameScene)
        frame.addKeyListener(newGameScene)
    }

    override fun getGraphics2D(): Graphics2D {
        if (isImageValid()) {
            createNewImage()
        }
        val g = createGraphics()
        return g
    }
}

class GameRunner(val window: IGameWindow, val gameScene: IGameScene){
    val fps: Double = 60.0

    companion object {
        var exitGame: Boolean = false
    }

    var currentGameScene: IGameScene = gameScene
        get() {
            return field
        }
        set(value) {
            currentGameScene.unload()
            window.onGameSceneChanged(currentGameScene, field)
            field = value
            field.load()
        }

    /**
     * This is used for testing purposes
     * @param numberOfIterations - The number of calls to update(), renderBuffer() and renderScreen() to run
     */
    fun run(numberOfIterations: Int){
        var i = 1
        while (i <= numberOfIterations && !exitGame){
            update()
            renderBuffer()
            renderScreen()

            i += 1
        }
    }

    fun run(){
        val period: Duration = (1 / fps).seconds
        var maxSkips: Int = 5
        var timeTaken: Duration
        while(!exitGame){
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

    private fun renderBuffer() {
        val g = window.getGraphics2D()
        currentGameScene.draw(g)
        g.dispose()
    }

    private fun renderScreen() {
        window.render()
    }
}