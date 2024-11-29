package Game

import Experimental.TerrainScene.Tank
import java.awt.Color

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
    var tanks: MutableList<Tank> = mutableListOf()
    var settings: MutableMap<String, Any> = mutableMapOf()

    var projectilesFlying = 0
    var explosionsActive = 0

    fun onGoingToMenu(){
        teams.clear()
        tanks.clear()
    }

    fun addTeam(t: Team){
        teams.add(t)
        tanks.addAll(t.tanks)
    }

    fun getCurrentTank(): Tank{
        val battleState = state as BattleState
        return battleState.currentTank(tanks)
    }

    fun nextPlayersTurn() {
        (state as BattleState).let {
            it.nextTurn(tanks)
        }
    }
}

class Team(val name: String, val tanks: List<Tank>) {
    var victories = 0
}

interface IState { }

class MenuState: IState {}
class SettingsState: IState{}
class CreatePlayersState: IState{}
class BattleState(): IState{
    var turnIndex = 0

    fun nextTurn(tanks: List<Tank>){
        val oldIndex = turnIndex
        while (true) {
            turnIndex = (turnIndex + 1).mod(tanks.size)
            if (tanks[turnIndex].alive) break
            if (turnIndex == oldIndex) break
        }
    }

    fun currentTank(tanks: List<Tank>): Tank{
        return tanks[turnIndex]
    }

    fun isBattleOver(): Boolean{
        var numTeamsWithAliveTanks = 0
        for (t in GameController.teams){
            if (t.tanks.any { it.alive }) {
                numTeamsWithAliveTanks += 1
                if (numTeamsWithAliveTanks > 1) return true
            }
        }
        return false
    }


}
class GameOverState: IState{}
class PurchaseState: IState{}