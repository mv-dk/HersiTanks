package Engine

import gameResX
import gameResY
import gameWindow
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.VolatileImage
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import javax.sound.sampled.*
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.concurrent.withLock
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
open class GameWindow(val width: Int, val height: Int, title: String, val gameScene: IGameScene, var fullScreen: Boolean) : Runnable, IGameWindow {
    var panel = JPanel()
    var frame: JFrame = JFrame()

    val renderingHints = java.util.Map.of(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    var gameRunner: GameRunner = GameRunner(this, gameScene)

    init {
        initFrameAndPanel(fullScreen)
    }

    fun initFrameAndPanel(fullScreen: Boolean){
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
        panel.setFocusTraversalKeysEnabled(false)
    }

    var image: VolatileImage = panel.createVolatileImage(screenWidth.toInt(), screenHeight.toInt())

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

    override fun render(gameSceneWidth: Int, gameSceneHeight: Int){
        if (panel.graphics == null) return
        val g2d = panel.graphics as Graphics2D
        val scaleX = screenWidth / gameSceneWidth
        val scaleY = screenHeight / gameSceneHeight
        val scale = Math.min(scaleX, scaleY)
        g2d.scale(scale, scale)
        g2d.translate((screenWidth / scale - gameSceneWidth) / 2, (screenHeight / scale - gameSceneHeight) / 2)

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
            createNewImage(true)
        }
        val g = createGraphics()
        return g
    }
}

class GameRunner(val window: IGameWindow, val gameScene: IGameScene) : KeyListener, MouseListener {
    private val keyEventQueue: ConcurrentLinkedQueue<KeyEvent> = ConcurrentLinkedQueue()
    private val mouseEventQueue: ConcurrentLinkedQueue<MouseEvent> = ConcurrentLinkedQueue()

    companion object {
        var exitGame: Boolean = false
        var fps: Double = 60.0
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
        if (e?.keyCode == KeyEvent.VK_F){
            gameWindow?.toggleFullScreen()
        }
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
                    handleKeyEvents()
                    handleMouseEvents()
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
        _lock.withLock {
            clipPlayer.loadSound(path, name)
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

    fun loadSound(path: String, name: String) {
        if (clipMap.containsKey(name)) return

        val audioStream = AudioSystem.getAudioInputStream(File(path))
        val audioClip = AudioSystem.getClip()
        audioClip.open(audioStream)
        audioInputStreamMap[name] = audioStream
        clipMap[name] = audioClip
    }

    fun playSound(name: String) {
        if (!clipMap.containsKey(name)) throw Exception("clip $name was not preloaded!")
        val clip = clipMap[name]!!
        println("active: ${clip.isActive}, running: ${clip.isRunning}, framePosition: ${clip.framePosition}")

        if (clip.isActive || clip.isRunning) {
            clip.stop()
        }
        if (clip.framePosition > 0) {
            clip.framePosition = 0
        }
        clip.start()
    }

    fun stopSound(name: String) {
        if (!clipMap.containsKey(name)) throw Exception("clip $name was not preloaded!")
        val clip = clipMap[name]!!
        clip.stop()
    }

    fun loopSound(name: String, times: Int = -1){
        clipMap[name]!!.loop(times)
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