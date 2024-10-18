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
}

/*
TODO: A globally accessible map of game objects by id. Or by name (string)?
TODO: Menu system
TODO: Sound effects. Music.
TODO: Scrolling if GameScene is larger than GameWindow
 */

