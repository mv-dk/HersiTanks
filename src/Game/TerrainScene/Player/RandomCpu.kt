package Game.TerrainScene.Player

import Game.GameController
import kotlin.random.Random

class RandomCpu : Cpu() {
    override fun getDecision(player: Player): PlayerDecision {
        repeat (Random.nextInt(10)) {
            player.cycleWeapon()
        }
        if (GameController.players.count { it.playing} <= 1) {
            return PlayerDecision(player, 0, 0, player.currentWeaponId)
        }

        val target = getRandomTargetExcept(player)
        val targetTank = target.tank
        val playersTank = player.tank

        if (targetTank == null || playersTank == null) return PlayerDecision(player, 0, 0, player.currentWeaponId)
        var minAngle = 0
        var maxAngle = 180
        var minPower = 50
        var maxPower = 300
        if (playersTank.position.x > targetTank.position.x) {
            minAngle = 91
        } else if (playersTank.position.x < targetTank.position.x) {
            maxAngle = 89
        }
        val distance = (playersTank.position.distance(targetTank.position))
        maxPower += distance.toInt()/10
        val decision = PlayerDecision(
            player,
            Random.nextInt(minAngle, maxAngle),
            Random.nextInt(minPower, maxPower),
            player.currentWeaponId
        )
        //println("Decision: angle ${decision.angle}, power ${decision.power}, weapon ${decision.weaponId}")
        return decision
    }
}