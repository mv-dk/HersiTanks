package Engine

import Game.BattleState
import Game.GameController
import Game.Menu.OPTION_WIND_NONE
import Game.Team
import Game.TerrainScene.Player.Player
import Game.TerrainScene.Player.PlayerType
import Game.TerrainScene.BattleScene
import Game.TerrainScene.Player.PlayerDecision
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.awt.Color
import java.awt.Graphics2D
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

        runHeadless(scene, 5)

        // Assert
        assertEquals(5, dummyObject.updateWasCalled)
        assertEquals(5, dummyObject.drawWasCalled)
    }

    @Test
    fun `Tank being hit by projectile must die`() {
        // Arrange
        val scene = StartBattleSceneWithOnePlayer()

        // Act
//        runWindowed(scene, 15_000)
        runHeadlessWhile(scene) {
            GameController.players.any { it.tank?.playing == true }
        }

        // Assert
        assert(GameController.players.any { it.tank?.energy == 0 })
    }

    /**
     * Make a BattleScene, and add one Player, having the color red,
     * and with 100 shots of weapon 2.
     */
    private fun StartBattleSceneWithOnePlayer(): BattleScene {
        Player("Bubu", PlayerType.LocalCpu).apply {
            color = Color.red
            weaponry.put(2, 100)

            GameController.players.add(this)
            GameController.teams.add(Team("BubuTeam", listOf(this)))
        }
        GameController.windOption = OPTION_WIND_NONE
        GameController.wind = 0.0

        val scene = BattleScene(
            640,
            getNextDecision = {
                PlayerDecision(it, 90, 100, 2)
            },
            tanksFallFromSky = false
        ).apply {
            load()
        }
        return scene
    }

    /**
     * Runs headless with an optional max number of iterations
     */
    fun runHeadless(scene: IGameScene, numberOfIterations: Int? = null) {
        val gameWindowMock = mock<IGameWindow>()
        val img = BufferedImage(1,1, BufferedImage.TYPE_INT_RGB)
        val g = img.graphics as Graphics2D
        mockitoWhen(gameWindowMock.getGraphics2D()).thenReturn(g)
        val gr = GameRunner(gameWindowMock, scene)

        if (numberOfIterations != null) {
            gr.run(numberOfIterations)
        } else {
            gr.run()
        }
    }

    /**
     * Run headless (only updates). After each update check if the given condition holds
     */
    fun runHeadlessWhile(scene: IGameScene, block: () -> Boolean) {
        val gameWindowMock = mock<IGameWindow>()
        val img = BufferedImage(1,1, BufferedImage.TYPE_INT_RGB)
        val g = img.graphics as Graphics2D
        mockitoWhen(gameWindowMock.getGraphics2D()).thenReturn(g)
        val gr = GameRunner(gameWindowMock, scene)

        gr.runUpdatesOnly(block)
    }

    /**
     * Run in a visible window. Makes debugging easier.
     */
    fun runWindowed(scene: IGameScene, timeoutMillis: Long) {
        val gameWindow = GameWindow(scene.width, scene.height, "HersiTest", scene, false)
        val gameThread = Thread(gameWindow)
        gameThread.start()

        val delayThread = Thread(object: Runnable {
            override fun run() {
                Thread.sleep(timeoutMillis)
                if (gameThread.isAlive) {
                    gameThread.interrupt()
                }
            }
        })
        delayThread.start()

        gameThread.join()
        if (delayThread.isAlive) {
            delayThread.interrupt()
        }
//        AudioHelper.unload()
        gameWindow.frame.dispose()
    }

    class DummyGameScene(color: Color, width: Int, height: Int): GameScene(color, width, height){
        override fun load() = Unit

        override fun keyTyped(e: KeyEvent) = Unit

        override fun keyPressed(e: KeyEvent) = Unit

        override fun keyReleased(e: KeyEvent) = Unit

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