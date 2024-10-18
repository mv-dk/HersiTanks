import Engine.GameWindow
import Experimental.Menu.MenuGameScene
import java.awt.Color

var _id: Int = 1
fun nextId(): Int { return _id++ }

fun main() {
    println("Hello World!")
    val gameScene = MenuGameScene(800, 600, Color.WHITE)
    //val gameScene = BallGameScene(width, height)
    //val gameScene = CollisionBallsGameScene(Color.LIGHT_GRAY, width, height)
    GameWindow(800, 600, "Hersi", gameScene).run()
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

TODO: KeyListener in GameRoom.
      Listen for onKeyDown, onKeyPressed, onKeyRepeated, onKeyUp. Something like that.

TODO: A globally accessible map of game objects by id. Or by name (string)?
TODO: Menu system
TODO: Sound effects. Music.
TODO: Scrolling if GameScene is larger than GameWindow
 */

