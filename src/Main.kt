import Engine.GameWindow
import Experimental.Menu.MenuGameScene
import java.awt.Color

var _id: Int = 1
fun nextId(): Int { return _id++ }

fun main() {
    println("Hello World!")
    GameWindow(800, 600, "Hersi").apply {
        //currentGameScene = BallGameScene(width, height)
        //currentGameScene = CollisionBallsGameScene(Color.LIGHT_GRAY, width, height)
        currentGameScene = MenuGameScene(width, height, Color.WHITE)
        run()
    }
}



