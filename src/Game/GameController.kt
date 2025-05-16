package Game

import Game.Menu.*
import Game.TerrainScene.Player.Player
import Game.TerrainScene.Tank
import gameResX

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
/**
 * GameController responsibilities:
 * - Hold game settings (wind, ground type, ground size)
 * - Hold players, teams, games to play, games played
 * - Remember how many projectiles are currently flying and how many explosions are active
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

    fun getCurrentPlayer(): Player? {
        val battleState = state as BattleState
        val currentPlayer = battleState.currentPlayer(players)
        return currentPlayer
    }

    fun getCurrentPlayersTank(): Tank?{
        return getCurrentPlayer()?.tank
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

class Team(val name: String, val players: List<Player>) {
    var victories = 0
}

interface IState { }

class MenuState: IState {}
class SettingsState: IState{}
class CreatePlayersState: IState{}
class BattleState(): IState{
    var turnIndex = 0

    fun nextTurn(players: List<Player>) {
        val oldIndex = turnIndex
        while (true) {
            turnIndex = (turnIndex + 1).mod(players.size)
            if (players[turnIndex].playing) break
            if (turnIndex == oldIndex) break
        }
    }

    fun currentPlayer(players: List<Player>): Player? {
        return players.getOrNull(turnIndex)
    }

    fun isBattleOver(): Boolean {
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