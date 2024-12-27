package Experimental.TerrainScene

import Engine.*
import Game.GameController
import java.awt.Color
import java.awt.Graphics2D

class Explosion(parent: IGameScene, position: Pos2D, var size: Int, val duration: Int, val onDone: () -> Unit) : GameObject2(parent, position) {
    var tick: Int = 0

    companion object {
        val currentExplosions = mutableSetOf<Explosion>()
    }

    init {
        GameController.explosionsActive += 1
        GameController.glowUp = 10
        val terrain = (parent as TerrainGameScene).rasterTerrain
        terrain.pokeHole(position.x.toInt(), position.y.toInt(), size)
        if (size > 100){
            AudioHelper.play("big-boom")
        } else {
            AudioHelper.play("small-boom")
        }

        Explosion.currentExplosions.add(this)
    }

    override fun update() {
        tick += 1
        if (tick ==1) {
            GameController.players.forEach {
                if (it.playing) {
                    val tank = it.tank
                    if (tank != null) {
                        val distance = Vec2D(position, tank.position).mag()
                        if (distance < size) {
                            val delta = Math.abs(20 * (size / distance).toInt())
                            tank.energy -= delta
                            GameController.getCurrentPlayer().money += Math.min(200, delta)
                            if (tank.energy < 0) {
                                tank.energy = 0
                                GameController.getCurrentPlayer().money += 100
                            }
                        }
                    }
                }
            }
        }
        if (duration - tick < 10) size = (size * 0.8).toInt()
        if (tick >= duration) {
            parent.remove(this)
            Explosion.currentExplosions.remove(this)
            GameController.explosionsActive -= 1
            onDone()
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = Color.RED
        g.fillOval((position.x - size/2).toInt(), (position.y - size/2).toInt(), size, size)
    }
}