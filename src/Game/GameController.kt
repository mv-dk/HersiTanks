package Game

import Experimental.TerrainScene.Tank

/*
    state (potential next states):

    Menu (Settings, CreatePlayers)
    Settings (Menu)
    CreatePlayers (Battle, Menu)
    Battle (Menu, Purchase, GameOver)
    GameOver (Menu, exit)
    Purchase (Purchase, Battle)
*/
object GameController {
    var state: IState = MenuState()
    var teams: MutableList<Team> = mutableListOf()
    var players: MutableList<Player> = mutableListOf()
    var settings: MutableMap<String, Any> = mutableMapOf()

    var projectilesFlying = 0
    var explosionsActive = 0

    fun onGoingToMenu(){
        teams.clear()
        players.clear()
    }

    fun addTeam(t: Team){
        teams.add(t)
        players.addAll(t.players)
    }

    fun getCurrentPlayersTank(): Tank?{
        val battleState = state as BattleState
        val currentPlayer = battleState.currentPlayer(players)
        return currentPlayer.tank
    }

    fun nextPlayersTurn() {
        (state as BattleState).let {
            it.nextTurn(players)
        }
    }
}

class Player(val name: String) {
    var tank: Tank? = null
    var playing: Boolean = true
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
                if (numTeamsWithAliveTanks > 1) return true
            }
        }
        return false
    }


}
class GameOverState: IState{}
class PurchaseState: IState{}