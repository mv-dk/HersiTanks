package Engine

import nextId
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_TEXT_ANTIALIASING
import java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import java.util.PriorityQueue
import java.util.SortedSet
import java.util.concurrent.ConcurrentLinkedQueue

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
    internal val gameObjectsByDrawOrder: SortedSet<IGameObject> = sortedSetOf(GameObjectDrawOrderComparator())
    private val gameObjectsToAdd: MutableMap<Int, IGameObject> = mutableMapOf()
    private val gameObjectsToRemove: MutableSet<Int> = mutableSetOf()
    var i = 0

    override fun add(gameObject: IGameObject) {
        gameObjectsToAdd.put(gameObject.id, gameObject)
        gameObjectsByDrawOrder.add(gameObject)
    }
    override fun remove(gameObject: IGameObject) {
        gameObjectsToRemove.add(gameObject.id)
        gameObjectsByDrawOrder.remove(gameObject)
    }

    override fun hasGameObjectWithId(id: Int): Boolean {
        return gameObjects.containsKey(id) && !gameObjectsToRemove.contains(id)
    }

    override fun forEachGameObject(act: (obj: IGameObject) -> Unit) = gameObjects.forEach { act(it.value) }
    final override fun unload() {
        forEachGameObject {
            it.unload()
        }
        if (gameObjectsToRemove.size < gameObjects.size) {
            println("GameScene is unloaded, but its ${gameObjects.size} objects are not removed.")
        }
    }

    override fun gameObjectsCount(): Int {
        return gameObjects.size
    }

    override fun update(){
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
        gameObjectsToRemove.clear()
        gameObjectsToAdd.clear()

        gameObjects.forEach {
            it.value.update()
        }
        i += 1
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.fillRect(0, 0, width, height)
        gameObjectsByDrawOrder.forEach { it.draw(g) }
    }

    private val renderingHints = mapOf(Pair(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON))

    override fun drawOnImage() : BufferedImage {
        var image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        var graphics = image.graphics as Graphics2D
        graphics.setRenderingHints(renderingHints)
        draw(graphics)
        return image
    }
}

fun drawAsHud(g:Graphics2D,  draw: (g:Graphics2D) -> Unit) {
    var oldTranslationX = g.transform.translateX
    var oldTranslationY = g.transform.translateY
    g.translate(-oldTranslationX, -oldTranslationY)

    draw(g)

    g.translate(oldTranslationX, oldTranslationY)
}