package Engine

import nextId
import java.awt.Color
import java.awt.Graphics2D

abstract class GameScene(val color: Color, override val width: Int, override val height:Int) : IGameScene {
    val id = nextId()
    private val gameObjects: MutableMap<Int, IGameObject> = mutableMapOf()
    private val gameObjectsToAdd: MutableMap<Int, IGameObject> = mutableMapOf()
    private val gameObjectsToRemove: MutableSet<Int> = mutableSetOf()

    override fun add(gameObject: IGameObject) { gameObjectsToAdd.put(gameObject.id, gameObject) }
    override fun remove(gameObject: IGameObject) { gameObjectsToRemove.add(gameObject.id) }

    override fun update(){
        gameObjects.forEach {
            it.value.update()
        }

        gameObjectsToRemove.forEach { gameObjects.remove(it) }
        gameObjects.putAll(gameObjectsToAdd)
        if (gameObjectsToAdd.size > 0 || gameObjectsToRemove.size > 0){
            println("GameObjects: ${gameObjects.size}")
        }
        gameObjectsToRemove.clear()
        gameObjectsToAdd.clear()
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillRect(0, 0, width, height)

        gameObjects.forEach {
            it.value.draw(g)
        }
    }
}