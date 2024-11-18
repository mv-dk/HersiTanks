package Engine

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import org.mockito.Mockito.`when` as mockitoWhen

class GameRunnerTest {

    @Test
    fun setCurrentGameScene() {

    }

    @Test
    fun `Objects added to scene must be updated when scene is updated`() {
        // Arrange
        val scene = DummyGameScene(Color.WHITE, 100, 100)
        val dummyObject = DummyGameObject(scene, Pos2D(0.0, 0.0))
        scene.add(dummyObject)

        val gameWindowMock = mock<IGameWindow>()
        val img = BufferedImage(1,1, BufferedImage.TYPE_INT_RGB)
        val g = img.graphics as Graphics2D
        mockitoWhen(gameWindowMock.getGraphics2D()).thenReturn(g)
        val gr = GameRunner(gameWindowMock, scene)

        // Act
        gr.run(5)

        // Assert
        assertEquals(5, dummyObject.updateWasCalled)
        assertEquals(5, dummyObject.drawWasCalled)
    }

    class DummyGameScene(color: Color, width: Int, height: Int): GameScene(color, width, height){
        override fun load() = Unit

        override fun keyTyped(e: KeyEvent?) = Unit

        override fun keyPressed(e: KeyEvent?) = Unit

        override fun keyReleased(e: KeyEvent?) = Unit

    }

    class DummyGameObject(parent: IGameScene, position: Pos2D):GameObject2(parent, position) {
        var updateWasCalled = 0
        var drawWasCalled = 0

        override fun update() {
            updateWasCalled += 1
        }

        override fun draw(g: Graphics2D) {
            drawWasCalled += 1
        }
    }
}