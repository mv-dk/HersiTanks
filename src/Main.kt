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

/*
TODO: There needs to be a lifecycle on GameObjects and on GameRooms.
      For example, what happens when a room is initiated? What happens when currentRoom changes to a different room -
      should any cleaning be done in the previous room? etc.
      For example, on GameRoom, this could be the life cycle:
       1. onInit
       2. (update / draw happens in game loop)
       3. onDelete
      .
      A lifecycle for GameObjects should also exist, for example:
       1. onInit
       2. (update / draw)
       3. onAddedToRoom / onRemovedFromRoom ?
       3. onDelete
      
 */

