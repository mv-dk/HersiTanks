package Game

import Experimental.TerrainScene.Tank
import java.awt.Color

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
    var state: IState = MenuState()
    var teams: MutableList<Team> = mutableListOf()
    var players: MutableList<Player> = mutableListOf()
    var gamesToPlay: Int = 10
    var gamesPlayed: Int = 0

    var projectilesFlying = 0
    var explosionsActive = 0
    var glowUp = 0 // whether everything should glow up

    fun onGoingToMenu(){
        teams.clear()
        players.clear()
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
        (state as BattleState).let {
            it.nextTurn(players)
        }
    }
}

class Player(var name: String) {
    var tank: Tank? = null
    var playing: Boolean = true
    var money = 0.0
    var color = Color.RED
    fun victories(): Int {
        return GameController.teams.find { it.players.contains(this) }?.victories ?: 0
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