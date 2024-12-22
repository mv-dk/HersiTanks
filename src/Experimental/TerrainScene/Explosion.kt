package Experimental.TerrainScene

import Engine.*
import Game.GameController
import java.awt.Color
import java.awt.Graphics2D

class Explosion(parent: IGameScene, position: Pos2D, var size: Int, val duration: Int, val onDone: () -> Unit) : GameObject2(parent, position) {
    var tick: Int = 0

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

    }

    override fun update() {
        tick += 1
        if (tick ==1) {
            GameController.players.filter{it.playing}.forEach {
                val tank = it.tank
                if (tank != null) {
                    val distance = Vec2D(position, tank.position).mag()
                    if (distance < size) {
                        val delta = Math.abs(20 * (size / distance).toInt())
                        println("Updating health for tank for ${it.name}), from ${tank.energy} to ${Math.max(tank.energy - delta, 0)}")
                        tank.energy -= delta
                        if (tank.energy < 0) {
                            tank.energy = 0
                        }
                    }
                }
            }
        }
        if (duration - tick < 10) size = (size * 0.8).toInt()
        if (tick >= duration) {
            parent.remove(this)
            GameController.explosionsActive -= 1
            onDone()
        }
    }

    override fun draw(g: Graphics2D) {
        g.color = Color.RED
        g.fillOval((position.x - size/2).toInt(), (position.y - size/2).toInt(), size, size)
    }
}