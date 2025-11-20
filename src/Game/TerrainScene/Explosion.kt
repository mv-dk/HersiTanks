package Game.TerrainScene

import Engine.*
import Engine.Audio.AudioHelper
import Game.particles.DirtFragmentEmitter
import Game.particles.FireEmitter
import Game.particles.SmokeEmitter
import Game.GameController
import java.awt.Color
import java.awt.Graphics2D
import SND_BIG_BOOM
import SND_SMALL_BOOM
import kotlin.math.abs
import kotlin.math.min

class Explosion(parent: IGameScene, position: Pos2D, var size: Int, val duration: Int, val onDone: () -> Unit) : GameObject2(parent, position) {
    var tick: Int = 0

    companion object {
        val currentExplosions = mutableSetOf<Explosion>()
    }

    init {
        GameController.explosionsActive += 1
        GameController.glowUp = 10
        val terrain = (parent as BattleScene).rasterTerrain
        terrain.pokeHole(position.x.toInt(), position.y.toInt(), size)
        terrain.addScorchFromExplosion(position.x.toInt()-(size/2), position.x.toInt()+(size/2))

        if (size >= 100){
            AudioHelper.play(SND_BIG_BOOM)
        } else {
            AudioHelper.play(SND_SMALL_BOOM)
        }

        currentExplosions.add(this)
        //parent.add(Transition(parent, 0.2))
        parent.add(SmokeEmitter(parent, position, size/2, 0.5))
        parent.add(FireEmitter(parent, position, size/2, 0.5))
        parent.add(DirtFragmentEmitter(parent, position, size/2))
    }

    override fun update() {
        tick += 1
        if (tick ==1) {
            GameController.players.forEach {
                if (it.playing) {
                    val tank = it.tank
                    if (tank != null) {
                        val distance = Vec2D(position, tank.position).mag()
                        if (distance < tank.size/2) { // direct hit
                            tank.energy = 0
                            if (GameController.getCurrentPlayersTank() != it.tank) {
                                GameController.getCurrentPlayer()?.let { p ->
                                    p.money += 100
                                }
                            }
                        } else if (distance < size*1.3) {
                            val delta = abs(20 * (size / distance).toInt())
                            tank.energy -= delta / 10
                            if (GameController.getCurrentPlayersTank() != it.tank) {
                                GameController.getCurrentPlayer()?.let { p ->
                                    p.money += min(200, delta)
                                }
                            }
                            if (tank.energy < 0) {
                                tank.energy = 0
                                if (GameController.getCurrentPlayersTank() != it.tank) {
                                    GameController.getCurrentPlayer()?.let { p ->
                                        p.money += 100
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (duration - tick < 10) size = (size * 0.8).toInt()
        if (tick >= duration) {
            parent.remove(this)
            currentExplosions.remove(this)
            GameController.explosionsActive -= 1
            onDone()
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = Color.RED
        g.fillOval((position.x - size/2).toInt(), (position.y - size/2).toInt(), size, size)
    }
}