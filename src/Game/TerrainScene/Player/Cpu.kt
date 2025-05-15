package Game.TerrainScene.Player

import Game.GameController

abstract class Cpu {
    abstract fun getDecision(player: Player): PlayerDecision

    fun isThereAPlayerOnMyLeft(me: Player) :Boolean {
        val myX: Double = me.tank?.position?.x ?: 0.0
        return GameController.players.filter{it.playing}.any {
            val itsX = it.tank?.position?.x ?: 0.0
            return itsX < myX
        }
    }

    fun isThereAPlayerOnMyRight(me: Player) :Boolean {
        val myX: Double = me.tank?.position?.x ?: 0.0
        return GameController.players.filter{it.playing}.any {
            val itsX = it.tank?.position?.x ?: 0.0
            return itsX > myX
        }
    }

    fun getRandomTargetExcept(exceptThis: Player) : Player {
        return GameController.players.filter {it.playing && it != exceptThis }.random()
    }
}