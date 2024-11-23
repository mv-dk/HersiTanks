import Engine.AudioHelper
import Engine.GameWindow
import Experimental.ManyBalls.BallGameScene
import Experimental.Menu.MenuGameScene
import java.awt.Color

var _id: Int = 1
fun nextId(): Int { return _id++ }

var gameWindow: GameWindow? = null

fun main() {
    println("Hello World!")
    preloadSounds()
    val gameScene = MenuGameScene(800, 600, Color.WHITE)
    //val gameScene = BallGameScene(800, 600)
    //val gameScene = CollisionBallsGameScene(Color.LIGHT_GRAY, width, height)
    gameWindow = GameWindow(800, 600, "Hersi", gameScene)
    val gameThread = Thread(gameWindow)
    gameThread.start()
    gameThread.join()
    AudioHelper.unload()
    gameWindow?.frame?.dispose()
}

fun preloadSounds(){
    AudioHelper.load("./resources/earthquake3.wav","earthquake")
    AudioHelper.load("./resources/boom3.wav", "small-boom")
    AudioHelper.load("./resources/boom2.wav", "big-boom")
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

