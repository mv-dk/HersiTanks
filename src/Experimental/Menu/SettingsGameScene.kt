package Experimental.Menu

import Engine.GameScene
import Engine.Pos2D
import Experimental.Menu.MenuPoints.MenuPointGameObject
import Experimental.Menu.MenuPoints.OptionSelectorMenuPoint
import Experimental.Menu.MenuPoints.OptionValue
import Game.GameController
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.event.KeyEvent

class SettingsGameScene : GameScene(Color(228, 217, 135), gameResX, gameResY) {

    val menuPoints = mutableListOf<MenuPointGameObject>(
        OptionSelectorMenuPoint("Sky", this,
            listOf(
                OptionValue(OPTION_SKY_BLUE, "Blue sky"),
                OptionValue(OPTION_SKY_STARRY, "Starry sky"),
                OptionValue(OPTION_SKY_EVENING, "Evening")
            ), OPTION_SKY_BLUE,
            { old,new ->
                GameController.skyOption = new.id
            }),
        OptionSelectorMenuPoint("Ground", this,
            listOf(
                OptionValue(OPTION_GROUND_GRASS, "Grass"),
                OptionValue(OPTION_GROUND_SNOW, "Snow")
            ), OPTION_GROUND_GRASS,
            {old,new ->
                GameController.groundOption = new.id
            }),
        OptionSelectorMenuPoint("Ground size", this,
            listOf(
                OptionValue(OPTION_GROUNDSIZE_SMALL, "Small"),
                OptionValue(OPTION_GROUNDSIZE_MEDIUM, "Medium"),
                OptionValue(OPTION_GROUNDSIZE_LARGE, "Large")
            ), OPTION_GROUNDSIZE_SMALL,
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
            ), OPTION_WIND_MEDIUM,
            { old, new ->
                GameController.windOption = new.id
            }),
        OptionSelectorMenuPoint("Decoration", this,
            listOf(
                OptionValue(OPTION_DECO_NONE, "None"),
                OptionValue(OPTION_DECO_CHRISTMAS, "Christmas hats"),
            ), OPTION_DECO_NONE,
            { old, new ->
                GameController.decorationOption = new.id
            }),
    )

    val menuGameObject = MenuGameObject(
        this,
        Pos2D(100.0, 20.0),
        400,
        400,
        30.0,
        menuPoints,
        {
            unload()
            gameWindow?.gameRunner?.currentGameScene = MenuGameScene()
        })

    init {
        add(menuGameObject)
    }

    override fun load() { }

    override fun keyTyped(e: KeyEvent) {
        menuGameObject.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        menuGameObject.keyPressed(e)
    }
}