package Game.TerrainScene.Player

import Engine.Pos2D
import Game.GameController
import Game.TerrainScene.ProjectileTrail
import Game.TerrainScene.Tank
import gameWindow
import java.awt.Color

class MonteCarloCpu(val repetitions: Int, val showDecisionOutcomes: Boolean): Cpu() {
    override fun getDecision(player: Player): PlayerDecision {
        player.tank?.let { tank ->
            val target = getRandomTargetExcept(player)
            val randomCpu = RandomCpu()
            var bestDecision = randomCpu.getDecision(player)
            var bestExplosionPosition = bestDecision.getSimulatedExplosionLocation(tank)
            var distance = bestExplosionPosition.distance(target.tank!!.position)
            repeat(repetitions) {
                val otherDecision = randomCpu.getDecision(player)
                val explosionPosition = otherDecision.getSimulatedExplosionLocation(tank)
                val otherDistance = explosionPosition.distance(target.tank!!.position)
                if (otherDistance < distance) {
                    bestDecision = otherDecision
                    bestExplosionPosition = explosionPosition.copy()
                    distance = otherDistance
                }
                if (showDecisionOutcomes) {
                    gameWindow?.gameRunner?.currentGameScene?.let {
                        it.add(ProjectileTrail(it, explosionPosition.copy(), Color.WHITE))
                    }
                }
            }
            if (showDecisionOutcomes) {
                gameWindow?.gameRunner?.currentGameScene?.let {
                    it.add(ProjectileTrail(it, bestExplosionPosition.copy(), Color.BLACK))
                }
            }
            return bestDecision
        }
        return RandomCpu().getDecision(player)
    }



    fun getDistanceToNearestAliveTankExcept(exceptThisTank: Tank, pos: Pos2D): Double {
        var minDistance = Double.MAX_VALUE
        for (player in GameController.players.filter { it.tank != exceptThisTank && it.playing && it.tank != null && (it.tank?.energy ?: 0) > 0  }) {
            val d = player.tank?.position?.distance(pos) ?: Double.MAX_VALUE
            if (d < minDistance) {
                minDistance = d
            }
        }
        return minDistance
    }
}