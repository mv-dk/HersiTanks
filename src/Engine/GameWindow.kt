package Engine

import Game.GameController
import gameResX
import gameResY
import java.awt.*
import java.awt.RenderingHints.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.image.VolatileImage
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import javax.sound.sampled.*
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.concurrent.withLock
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.times


interface IGameWindow {
    fun onGameSceneChanged(oldGameScene: IGameScene, newGameScene: IGameScene)
    fun getGraphics2D(): Graphics2D
    fun render(width: Int, height: Int)
}
var screenWidth = gameResX.toDouble()
var screenHeight = gameResY.toDouble()
/**
 * Creates a JFrame and a JPanel.
 * Contains the game loop in the function run().
 * Contains currentGameScene, on which update() and draw() is called.
 */
open class GameWindow(
    val width: Int,
    val height: Int,
    title: String,
    private val gameScene: IGameScene,
    private var fullScreen: Boolean
) : Runnable, IGameWindow {
    private var panel = JPanel()
    var frame: JFrame = JFrame()

    private val renderingHints = mapOf<Key, Any>(
        KEY_TEXT_ANTIALIASING to VALUE_TEXT_ANTIALIAS_ON
    )

    var gameRunner: GameRunner = GameRunner(this, gameScene)
    private var scale = 1.0

    init {
        initFrameAndPanel(fullScreen)
    }

    private fun initFrameAndPanel(fullScreen: Boolean){
        panel.preferredSize = Dimension(width, height)
        panel.size = Dimension(width, height)
        panel.isFocusable = true
        panel.requestFocusInWindow()

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        if (fullScreen) {
            frame.isUndecorated = true
            val graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment()
            val device = graphicsEnv.defaultScreenDevice
            if (device.isFullScreenSupported) {
                device.fullScreenWindow = frame
                screenWidth = device.fullScreenWindow.width.toDouble()
                screenHeight = device.fullScreenWindow.height.toDouble()
            }
        } else {
            screenWidth = width.toDouble()
            screenHeight = height.toDouble()
        }

        frame.add(panel)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
        panel.addKeyListener(gameRunner)
        panel.addMouseListener(gameRunner)
        panel.addMouseMotionListener(gameRunner)
        panel.setFocusTraversalKeysEnabled(false)
    }

    private var image: VolatileImage = panel.createVolatileImage(screenWidth.toInt(), screenHeight.toInt())

    fun toggleFullScreen(){
        frame.dispose()
        frame = JFrame()
        panel = JPanel()

        if (fullScreen) {
            fullScreen = false
            GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
            initFrameAndPanel(false)
            image = panel.createVolatileImage(width, height)
        } else {
            fullScreen = true
            initFrameAndPanel(true)
            image = panel.createVolatileImage(screenWidth.toInt(), screenHeight.toInt())
        }
    }

    private fun isImageValid(): Boolean {
        return image.validate(panel.graphicsConfiguration) == VolatileImage.IMAGE_INCOMPATIBLE;
    }

    private fun createNewImage(isFullScreen: Boolean) {
        if (isFullScreen) {
            image = panel.createVolatileImage(screenWidth.toInt(), screenHeight.toInt())
        } else {
            image = panel.createVolatileImage(width, height)
        }
    }

    private fun createGraphics():Graphics2D {
        val g = image.createGraphics()
        g.setRenderingHints(renderingHints)
        return g;
    }

    override fun render(width: Int, height: Int){
        if (panel.graphics == null) return
        val g2d = panel.graphics as Graphics2D
        val scaleX = screenWidth / width
        val scaleY = screenHeight / height
        scale = min(scaleX, scaleY)
        g2d.scale(scale, scale)
        g2d.translate((screenWidth / scale - width) / 2, (screenHeight / scale - height) / 2)

        g2d.drawImage(image, 0, 0, null)
        Toolkit.getDefaultToolkit().sync()
    }

    override fun run() {
        gameRunner.run()
    }

    override fun onGameSceneChanged(oldGameScene: IGameScene, newGameScene: IGameScene) {

    }

    override fun getGraphics2D(): Graphics2D {
        if (isImageValid()) {
            createNewImage(fullScreen)
        }
        val g = createGraphics()
        return g
    }
}

class GameRunner(
    private val window: IGameWindow,
    private val gameScene: IGameScene
) : KeyListener, MouseListener, MouseMotionListener {
    private val keyEventQueue: ConcurrentLinkedQueue<KeyEvent> = ConcurrentLinkedQueue()
    private val mouseEventQueue: ConcurrentLinkedQueue<MouseEvent> = ConcurrentLinkedQueue()

    companion object {
        val debug = true
        var exitGame = false
        var fps = 60.0

        var tick = 1.0/fps // time for one cycle
    }

    var currentGameScene: IGameScene = gameScene
        get() {
            return field
        }
        set(value) {
            currentGameScene.unload()
            window.onGameSceneChanged(currentGameScene, value)
            field = value
            field.load()
        }

    override fun keyTyped(e: KeyEvent?) {
        keyEventQueue.add(e)
    }

    override fun keyPressed(e: KeyEvent?) {
        keyEventQueue.add(e)
    }

    override fun keyReleased(e: KeyEvent?) {
        keyEventQueue.add(e)
    }

    override fun mouseClicked(e: MouseEvent?) {
        mouseEventQueue.add(e)
    }

    override fun mousePressed(e: MouseEvent?) {
        mouseEventQueue.add(e)
    }

    override fun mouseReleased(e: MouseEvent?) {
        mouseEventQueue.add(e)
    }

    override fun mouseEntered(e: MouseEvent?) {
        mouseEventQueue.add(e)
    }

    override fun mouseExited(e: MouseEvent?) {
        mouseEventQueue.add(e)
    }

    override fun mouseDragged(e: MouseEvent?) {
        mouseEventQueue.add(e)
    }

    override fun mouseMoved(e: MouseEvent?) {
        mouseEventQueue.add(e)
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

    fun runUpdatesOnly(whilePredicate: () -> Boolean) {
        do {
            update()
        } while (whilePredicate())
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
                    val updateTime = measureTime {
                        update()
                    }
                    GameController.updateTime = updateTime.inWholeMilliseconds
                    handleKeyEvents()
                    handleMouseEvents()
                    if (updatesPerDraw == 1) {
                        val renderBufferTime = measureTime {
                            renderBuffer()
                        }
                        val renderScreenTime = measureTime {
                            renderScreen()
                        }
                        GameController.renderBufferTime = renderBufferTime.inWholeMilliseconds
                        GameController.renderScreenTime = renderScreenTime.inWholeMilliseconds
                    }
                }
                if (timeTaken < (updatesPerDraw * period)){
                    //if (updatesPerDraw > 1) { println("Skipped ${updatesPerDraw-1} draws") }
                    Thread.sleep(((updatesPerDraw * period) - timeTaken).inWholeMilliseconds)
                    break;
                }
                updatesPerDraw += 1
            }
        }
    }

    fun handleKeyEvents(){
        var keyEvent = keyEventQueue.poll()
        while (keyEvent != null) {
            when (keyEvent.id) {
                KeyEvent.KEY_PRESSED -> currentGameScene.keyPressed(keyEvent)
                KeyEvent.KEY_TYPED -> currentGameScene.keyTyped(keyEvent)
                KeyEvent.KEY_RELEASED -> currentGameScene.keyReleased(keyEvent)
            }
            keyEvent = keyEventQueue.poll()
        }
    }

    fun handleMouseEvents(){
        var mouseEvent = mouseEventQueue.poll()
        while (mouseEvent != null) {
            when (mouseEvent.id) {
                MouseEvent.MOUSE_PRESSED -> currentGameScene.mousePressed(mouseEvent)
                MouseEvent.MOUSE_MOVED -> currentGameScene.mouseMoved(mouseEvent)
                MouseEvent.MOUSE_CLICKED -> currentGameScene.mouseClicked(mouseEvent)
                MouseEvent.MOUSE_RELEASED -> currentGameScene.mouseReleased(mouseEvent)
                MouseEvent.MOUSE_ENTERED -> currentGameScene.mouseEntered(mouseEvent)
                MouseEvent.MOUSE_EXITED -> currentGameScene.mouseExited(mouseEvent)
                MouseEvent.MOUSE_WHEEL -> currentGameScene.mouseWheel(mouseEvent)
            }
            mouseEvent = mouseEventQueue.poll()
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
        window.render(currentGameScene.width, currentGameScene.height)
    }
}

object AudioHelper {
    private val clipPlayer = AudioClipPlayer()
    private val _lock = ReentrantLock()

    fun load(path: String, name: String){
        val url = AudioHelper.javaClass.classLoader.getResource(path)
        _lock.withLock {
            clipPlayer.loadSound(url, name)
        }
    }

    fun play(name: String) {
        _lock.withLock {
            clipPlayer.playSound(name)
        }
    }

    fun stop(name: String){
        _lock.withLock {
            clipPlayer.stopSound(name)
        }
    }

    fun loop(name: String, times: Int = -1){
        _lock.withLock {
            clipPlayer.loopSound(name, times)
        }
    }

    fun unload(){
        _lock.withLock {
            clipPlayer.unload()
        }
    }
}

class AudioClipPlayer {
    private var audioInputStreamMap = mutableMapOf<String, AudioInputStream>()
    private var clipMap = mutableMapOf<String, Clip>()

    fun loadSound(path: URL, name: String) {
        if (clipMap.containsKey(name)) return

        val audioStream = AudioSystem.getAudioInputStream(path)
        val audioClip = AudioSystem.getClip()
        audioClip.open(audioStream)
        audioInputStreamMap[name] = audioStream
        clipMap[name] = audioClip
    }

    fun playSound(name: String) {
         clipMap[name]?.let { clip ->
             if (clip.isRunning) {
                 clip.stop()
                 clip.flush()
             }
             clip.framePosition = 0
             while (!clip.isRunning)
                 clip.start()

         }
    }

    fun stopSound(name: String) {
        clipMap[name]?.let { clip ->
            while (clip.isRunning) {
                clip.stop()
                clip.flush()
                clip.framePosition = 0
            }
        }
    }

    fun loopSound(name: String, times: Int = -1){
        clipMap[name]?.loop(times)
    }

    fun unload(){
        for (clip in clipMap){
            clip.value.close()
        }
        for (audioStream in audioInputStreamMap){
            audioStream.value.close()
        }
    }
}