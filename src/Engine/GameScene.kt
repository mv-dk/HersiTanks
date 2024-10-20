package Engine

import nextId
import java.awt.Color
import java.awt.Graphics2D
import java.util.PriorityQueue
import java.util.SortedSet

class GameObjectDrawOrderComparator: Comparator<IGameObject> {
    override fun compare(o1: IGameObject?, o2: IGameObject?): Int {
        if (o1 != null && o2 != null) {
            if (o1.drawOrder == o2.drawOrder) return o1.id.compareTo(o2.id)
            return o1.drawOrder.compareTo(o2.drawOrder)
        }
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return 1;
        return -1;
    }
}

abstract class GameScene(val color: Color, override val width: Int, override val height:Int) : IGameScene {
    val id = nextId()
    private val gameObjects: MutableMap<Int, IGameObject> = mutableMapOf()
    private val gameObjectsByDrawOrder: SortedSet<IGameObject> = sortedSetOf(GameObjectDrawOrderComparator())
    private val gameObjectsToAdd: MutableMap<Int, IGameObject> = mutableMapOf()
    private val gameObjectsToRemove: MutableSet<Int> = mutableSetOf()

    override fun add(gameObject: IGameObject) {
        gameObjectsToAdd.put(gameObject.id, gameObject)
        gameObjectsByDrawOrder.add(gameObject)
    }
    override fun remove(gameObject: IGameObject) {
        gameObjectsToRemove.add(gameObject.id)
        gameObjectsByDrawOrder.remove(gameObject)
    }

    override fun forEachGameObject(act: (obj: IGameObject) -> Unit) = gameObjects.forEach { act(it.value) }
    override fun unload() = forEachGameObject { it.unload() }

    override fun update(){
        gameObjects.forEach {
            it.value.update()
        }

        gameObjectsToRemove.forEach {
            gameObjects[it]?.onBeforeRemoved()
            gameObjectsByDrawOrder.remove(gameObjects[it])
            gameObjects.remove(it)?.onAfterRemoved()
        }
        gameObjects.putAll(gameObjectsToAdd)
        gameObjectsByDrawOrder.addAll(gameObjectsToAdd.values)
        gameObjectsToAdd.forEach {
            it.value.onAdded()
        }
        if (gameObjectsToAdd.size > 0 || gameObjectsToRemove.size > 0){
            println("GameObjects: ${gameObjects.size}")
        }
        gameObjectsToRemove.clear()
        gameObjectsToAdd.clear()
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillRect(0, 0, width, height)
        gameObjectsByDrawOrder.forEach { it.draw(g) }
    }
}