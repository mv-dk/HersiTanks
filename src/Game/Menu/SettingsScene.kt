package Game.Menu

import Engine.GameScene
import Engine.Pos2D
import Game.GameController
import Game.Menu.MenuPoints.*
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent

class SettingsScene() : GameScene(Color(228, 217, 135), gameResX, gameResY) {

    val menuPoints = mutableListOf<MenuPointGameObject>(
        OptionSelectorMenuPoint("Sky", this,
            listOf(
                OptionValue(OPTION_SKY_RANDOM, "Random"),
                OptionValue(OPTION_SKY_BLUE, "Blue sky"),
                OptionValue(OPTION_SKY_STARRY, "Starry sky"),
                OptionValue(OPTION_SKY_EVENING, "Evening")
            ), 0,
            { old,new ->
                GameController.skyOption = new.id
            }),
        OptionSelectorMenuPoint("Ground", this,
            listOf(
                OptionValue(OPTION_GROUND_RANDOM, "Random"),
                OptionValue(OPTION_GROUND_GRASS, "Grass"),
                OptionValue(OPTION_GROUND_SNOW, "Snow")
            ), 0,
            {old,new ->
                GameController.groundOption = new.id
            }),
        OptionSelectorMenuPoint("Ground size", this,
            listOf(
                OptionValue(OPTION_GROUNDSIZE_SMALL, "Small"),
                OptionValue(OPTION_GROUNDSIZE_MEDIUM, "Medium"),
                OptionValue(OPTION_GROUNDSIZE_LARGE, "Large")
            ), GameController.groundSizeOption,
            {old,new ->
                GameController.groundSizeOption = new.id
                GameController.groundSize = when (new.id) {
                    OPTION_GROUNDSIZE_MEDIUM -> gameResX*2
                    OPTION_GROUNDSIZE_LARGE -> gameResX*4
                    else -> gameResX
                }
            }),
        OptionSelectorMenuPoint("Wind", this,
            listOf(
                OptionValue(OPTION_WIND_NONE, "None"),
                OptionValue(OPTION_WIND_LIGHT, "Breeze"),
                OptionValue(OPTION_WIND_MEDIUM, "Medium"),
                OptionValue(OPTION_WIND_STRONG, "Stormy")
            ), GameController.windOption,
            { old, new ->
                GameController.windOption = new.id
            }),
        OptionSelectorMenuPoint("Decoration", this,
            listOf(
                OptionValue(OPTION_DECO_NONE, "None"),
                OptionValue(OPTION_DECO_CHRISTMAS, "Christmas hats"),
            ), GameController.decorationOption,
            { old, new ->
                GameController.decorationOption = new.id
            }),
        ToggleFullScreenMenuPoint(this),
        ChangeSceneMenuPoint("Done", this, { MenuScene() })
    )

    val menuGameObject = MenuGameObject(
        this,
        Pos2D(100.0, 20.0),
        400,
        400,
        30.0,
        20.0,
        menuPoints,
        {
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuScene()
        })

    init {
        repeat(10) {
            add(FloatingBlob(this))
        }

        add(menuGameObject)
        add(Transition(this))
    }

    override fun load() { }

    override fun keyTyped(e: KeyEvent) {
        menuGameObject.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        menuGameObject.keyPressed(e)
    }

    override fun draw(g: Graphics2D) {
        super.draw(g)
    }

    override fun update() {
        super.update()
    }
}