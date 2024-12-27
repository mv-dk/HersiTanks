import Engine.AudioHelper
import Engine.GameWindow
import Experimental.ManyBalls.BallGameScene
import Experimental.Menu.MenuGameScene
import java.awt.Color
import java.awt.GraphicsEnvironment
import kotlin.math.min

var _id: Int = 1
fun nextId(): Int { return _id++ }

var gameWindow: GameWindow? = null

/**
 * Global game resolution.
 * It is best to create the scenes in this size.
 * Even if the window is bigger, the rendering will be this size (it is scaled to fit the window).
 */
var gameResX = 640
var gameResY = 360

var menuGameScene = MenuGameScene(gameResX, gameResY, Color(77, 83, 128))

fun main() {
    println("Hello World!")
    preloadSounds()
    //val gameScene = BallGameScene(800, 600)
    //val gameScene = CollisionBallsGameScene(Color.LIGHT_GRAY, width, height)

    val scale = getBestScale()

    gameWindow = GameWindow(gameResX * scale, gameResY * scale, "Hersi", menuGameScene, false)
    val gameThread = Thread(gameWindow)
    gameThread.start()
    gameThread.join()
    AudioHelper.unload()
    gameWindow?.frame?.dispose()
}

/**
 * Returns the max integer scale factor for the window, for it to be fully contained
 * in the default screen device
 */
fun getBestScale() : Int{
    val scaleX = ((GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayMode.width-1) / gameResX)
    val scaleY = ((GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayMode.height-1) / gameResY);
    val scale = min(scaleX, scaleY)
    return scale
}

fun preloadSounds(){
    AudioHelper.load("earthquake3.wav","earthquake")
    AudioHelper.load("hersi-eksplosion-01.wav", "small-boom")
    AudioHelper.load("boom3.wav", "big-boom")
    AudioHelper.load("vhup.wav", "fire")
    AudioHelper.load("hersi-vinkel.wav", "change-angle")
    AudioHelper.load("increase_power.wav", "increase-power")
    AudioHelper.load("decrease_power.wav", "decrease-power")
    AudioHelper.load("fjuj.wav", "fizzle")
    AudioHelper.load("pew1.wav", "fire2")
    AudioHelper.load("pew2.wav", "fire3")
}

/*
TODO: Menu system
      1. A number selector (done)
      2. A text input (done)
      3. A go-to-scene button (done)
      4. A show-dialog selector
      5. Number values and special chars in text input (done)
TODO: A globally accessible map of game objects by id. Or by name (string)?
TODO: Sound effects. Music.
      - "Clip" is used for short sound clips. The whole sound is loaded
        into memory, and playback can start (from anywhere) and stop at any time,
        as well as loop.
      - "SourceDataLine" is a buffered or streaming sound API. It can be used
        to play longer sound files that cannot be preloaded into memory. It is
        also more memory efficient (could be used for background music). It can
        also be used if streaming music over the network.
TODO: Scrolling if GameScene is larger than GameWindow
TODO: Sprites
      1. Static sprites
      2. Change sprite
      3. Animated sprites
      4. Change animation
 */

