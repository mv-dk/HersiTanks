import Engine.AudioHelper
import Engine.GameWindow
import Game.Menu.MenuScene
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

fun main() {
    println("Hello World!")
    preloadSounds()
    //val gameScene = BallGameScene(800, 600)
    //val gameScene = CollisionBallsGameScene(Color.LIGHT_GRAY, width, height)

    val scale = getBestScale()

    gameWindow = GameWindow(gameResX * scale, gameResY * scale, "Hersi", MenuScene(), false)
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

const val SND_EARTHQUAKE = "earthquake"
const val SND_SMALL_BOOM = "small-boom"
const val SND_BIG_BOOM = "big-boom"
const val SND_FIRE = "fire"
const val SND_CHANGE_ANGLE = "change-angle"
const val SND_INCREASE_POWER = "increase-power"
const val SND_DECREASE_POWER = "decrease-power"
const val SND_FIZZLE = "fizzle"
const val SND_FIRE2 = "fire2"
const val SND_FIRE3 = "fire3"
const val SND_BUY = "buy"
const val SND_BUY_FINISH = "buy-finish"
const val SND_BAMBOO_01 = "bamboo01"
const val SND_BAMBOO_02 = "bamboo02"
const val SND_BAMBOO_03 = "bamboo03"
const val SND_SWOOSH = "swoosh"
const val SND_SWOOSH2 = "swoosh2"

fun preloadSounds(){
    AudioHelper.load("earthquake3.wav",SND_EARTHQUAKE)
    AudioHelper.load("hersi-eksplosion-01.wav", SND_SMALL_BOOM)
    AudioHelper.load("boom3.wav", SND_BIG_BOOM)
    AudioHelper.load("vhup.wav", SND_FIRE)
    AudioHelper.load("hersi-vinkel.wav", SND_CHANGE_ANGLE)
    AudioHelper.load("increase_power.wav", SND_INCREASE_POWER)
    AudioHelper.load("decrease_power.wav", SND_DECREASE_POWER)
    AudioHelper.load("fjuj.wav", SND_FIZZLE)
    AudioHelper.load("pew1.wav", SND_FIRE2)
    AudioHelper.load("pew2.wav", SND_FIRE3)
    AudioHelper.load("buy01.wav", SND_BUY)
    AudioHelper.load("buy-finish.wav", SND_BUY_FINISH)
    AudioHelper.load("bamboo01.wav", SND_BAMBOO_01)
    AudioHelper.load("bamboo02.wav", SND_BAMBOO_02)
    AudioHelper.load("bamboo03.wav", SND_BAMBOO_03)
    AudioHelper.load("swoosh.wav", SND_SWOOSH) // https://freesound.org/people/lesaucisson/sounds/585257/
    AudioHelper.load("swoosh2.wav", SND_SWOOSH2) // https://freesound.org/people/Benboncan/sounds/74692/
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

