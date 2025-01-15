package Game

import Engine.AudioHelper
import Engine.GameWindow
import Engine.Pos2D
import Engine.Vec2D
import Experimental.Menu.OPTION_DECO_NONE
import Experimental.Menu.OPTION_GROUNDSIZE_SMALL
import Experimental.Menu.OPTION_GROUND_GRASS
import Experimental.Menu.OPTION_SKY_BLUE
import Experimental.Menu.OPTION_WIND_MEDIUM
import Experimental.TerrainScene.Projectile
import Experimental.TerrainScene.ProjectileTrail
import Experimental.TerrainScene.Tank
import Experimental.TerrainScene.Weapon
import SND_FIRE
import SND_FIRE2
import SND_FIRE3
import SND_FIZZLE
import gameResX
import gameWindow
import java.awt.Color
import kotlin.random.Random

/*
    state (potential next states):

    Menu (Settings, CreatePlayers)
    Settings (Menu)
    CreatePlayers (Battle, Menu)
    Battle (Menu, Status, Purchase, GameOver)
    Status (Purchase, Battle, Menu)
    GameOver (Menu, exit)
    Purchase (Purchase, Battle)
*/
object GameController {
    var numberOfPlayersOption: Int = 2
    var updateTime: Long = 0
    var renderBufferTime: Long = 0
    var renderScreenTime: Long = 0
    var decorationOption: Int = OPTION_DECO_NONE
    var wind: Double = 0.0
    var windOption: Int = OPTION_WIND_MEDIUM
    var groundOption: Int = OPTION_GROUND_GRASS
    var skyOption: Int = OPTION_SKY_BLUE
    var groundSizeOption: Int = OPTION_GROUNDSIZE_SMALL
    var groundSize: Int = gameResX
    var state: IState = MenuState()
    var teams: MutableList<Team> = mutableListOf()
    var players: MutableList<Player> = mutableListOf()
    var gamesToPlay: Int = 10
    var gamesPlayed: Int = 0

    var projectilesFlying = 0
    var explosionsActive = 0
    var glowUp = 0 // whether everything should glow up

    fun onGoingToMenu(){

    }

    fun addTeam(t: Team){
        teams.add(t)
        players.addAll(t.players)
    }

    fun getCurrentPlayer(): Player {
        val battleState = state as BattleState
        val currentPlayer = battleState.currentPlayer(players)
        return currentPlayer
    }

    fun getCurrentPlayersTank(): Tank?{
        return getCurrentPlayer().tank
    }

    fun getCurrentPlayersTeam(): Team {
        val currentPlayer = getCurrentPlayer()
        val currentPlayersTeam = teams.find { it.players.contains(currentPlayer) }
        if (currentPlayersTeam == null) throw Exception("Current player does not have any team")
        return currentPlayersTeam
    }

    fun nextPlayersTurn() {
        (state as BattleState).nextTurn(players)
    }
}

class Player(var name: String, val playerType: PlayerType) {
    var tank: Tank? = null
    var playing: Boolean = true
    var weaponry = mutableMapOf<Int,Int>() // Map from weaponId to ammo
    var money = 200.0
    var color = Color.RED
    var currentWeaponId = 1

    fun victories(): Int {
        return GameController.teams.find { it.players.contains(this) }?.victories ?: 0
    }

    fun decreaseAmmoAndCycleIfZero() {
        weaponry[currentWeaponId] = (weaponry[currentWeaponId] ?: 1) -1
        if ((weaponry[currentWeaponId] ?: 0) == 0) {
            cycleWeapon()
        }
    }

    fun cycleWeapon() {
        val oldWeaponId = currentWeaponId
        while (true) {
            currentWeaponId += 1
            if (currentWeaponId > Weapon.maxWeaponId) currentWeaponId = Weapon.minWeaponId
            if (weaponry.containsKey(currentWeaponId) && (weaponry[currentWeaponId] ?: 0) > 0) break
            if (currentWeaponId == oldWeaponId) break
        }
    }

    fun fire() {
        when (Random.nextInt(3)) {
            0 -> AudioHelper.play(SND_FIRE)
            1 -> AudioHelper.play(SND_FIRE2)
            2 -> AudioHelper.play(SND_FIRE3)
        }

        val player = GameController.getCurrentPlayer()

        if ((player.weaponry[player.currentWeaponId] ?: 0) == 0) {
            AudioHelper.play(SND_FIZZLE);
        } else {
            tank?.let {
                val velocity = it.getFireVelocity()
                val position = Pos2D(it.canonX.toDouble(), it.canonY.toDouble())
                val projectile = Weapon.allWeapons[player.currentWeaponId]?.getProjectile(it.parent, position, velocity)
                if (projectile != null) {
                    it.parent.add(projectile)
                }
                player.decreaseAmmoAndCycleIfZero()
            }
        }
    }
}

class PlayerDecision(
    var player: Player,
    var angle: Int,
    var power: Int,
    var weaponId: Int) {

    fun isValid(): Boolean {
        return (player.weaponry[weaponId] ?: 0) > 0
    }

    fun getSimulatedExplosionLocation(tank: Tank): Pos2D{
        val pos = tank.position.copy()
        var done = false
        val explosionPosition = Pos2D(0.0, 0.0)
        gameWindow?.gameRunner?.currentGameScene?.let {gameScene ->
            val canonX = (tank.position.x + tank.size * Math.cos(Math.PI*angle/180.0)).toInt()
            val canonY = (tank.position.y - tank.size * Math.sin(Math.PI*angle/180.0)).toInt()
            val velocity = Vec2D(
                pos.copy(),
                Pos2D(canonX.toDouble(), canonY.toDouble())
            ).times(power / 400.0)
            val projectile = Projectile(gameScene, Pos2D(canonX.toDouble(), canonY.toDouble()), velocity, 0, simulated = true, onExplode = { position ->
                explosionPosition.x = position.x
                explosionPosition.y = position.y
                done = true
            })
            while (!done) {
                projectile.update()
            }
        }
        return explosionPosition
    }
}

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

    fun getRandomTargetExcept(exceptThis: Player) :Player {
        return GameController.players.filter {it.playing && it != exceptThis }.random()
    }
}

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
        val decision = PlayerDecision(player, Random.nextInt(minAngle, maxAngle), Random.nextInt(minPower, maxPower), player.currentWeaponId)
        //println("Decision: angle ${decision.angle}, power ${decision.power}, weapon ${decision.weaponId}")
        return decision
    }
}

class MonteCarloCpu(val showDecisionOutcomes: Boolean): Cpu() {
    override fun getDecision(player: Player): PlayerDecision {
        player.tank?.let { tank ->
            val target = getRandomTargetExcept(player)
            val randomCpu = RandomCpu()
            var bestDecision = randomCpu.getDecision(player)
            var bestExplosionPosition = bestDecision.getSimulatedExplosionLocation(tank)
            var distance = bestExplosionPosition.distance(target.tank!!.position)
            repeat(10) {
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

enum class PlayerType {
    LocalHuman, LocalCpu, NetworkHuman, NetworkCpu
}

class Team(val name: String, val players: List<Player>) {
    var victories = 0
}

interface IState { }

class MenuState: IState {}
class SettingsState: IState{}
class CreatePlayersState: IState{}
class BattleState(): IState{
    var turnIndex = 0

    fun nextTurn(players: List<Player>){
        val oldIndex = turnIndex
        while (true) {
            turnIndex = (turnIndex + 1).mod(players.size)
            if (players[turnIndex].playing) break
            if (turnIndex == oldIndex) break
        }
    }

    fun currentPlayer(players: List<Player>): Player{
        return players[turnIndex]
    }

    fun isBattleOver(): Boolean{
        var numTeamsWithAliveTanks = 0
        for (t in GameController.teams){
            if (t.players.any { it.playing }) {
                numTeamsWithAliveTanks += 1
                if (numTeamsWithAliveTanks > 1) return false
            }
        }
        return true
    }


}
class StatusState: IState{}
class GameOverState: IState{}
class PurchaseState: IState{}