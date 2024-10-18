import Engine.GameWindow
import Experimental.ManyBalls.BallGameScene
import Experimental.Menu.MenuGameScene
import java.awt.Color

var _id: Int = 1
fun nextId(): Int { return _id++ }

fun main() {
    println("Hello World!")
    val gameScene = MenuGameScene(800, 600, Color.WHITE)
    //val gameScene = BallGameScene(800, 600)
    //val gameScene = CollisionBallsGameScene(Color.LIGHT_GRAY, width, height)
    val gameWindow = GameWindow(800, 600, "Hersi", gameScene)
    val gameThread = Thread(gameWindow)
    gameThread.start()
    gameThread.join()
    gameWindow.frame.dispose()
}

/*
TODO: Menu system
      1. A number selector
      2. A text input
      3. A go-to-scene button
TODO: A globally accessible map of game objects by id. Or by name (string)?
TODO: Sound effects. Music.
TODO: Scrolling if GameScene is larger than GameWindow
 */

