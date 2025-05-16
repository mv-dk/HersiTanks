package Game.TerrainScene.Player

import Engine.Pos2D
import Engine.Vec2D
import Game.TerrainScene.Projectile
import Game.TerrainScene.Tank
import gameWindow
import java.awt.Color

class PlayerDecision(
    var player: Player,
    var angle: Int,
    var power: Int,
    var weaponId: Int) {

    fun isValid(): Boolean {
        return (player.weaponry[weaponId] ?: 0) > 0
    }

    fun getSimulatedExplosionLocation(tank: Tank): Pos2D {
        val pos = tank.position.copy()
        var done = false
        val explosionPosition = Pos2D(0.0, 0.0)
        gameWindow?.gameRunner?.currentGameScene?.let { gameScene ->
            val canonX = (tank.position.x + tank.size * Math.cos(Math.PI*angle/180.0)).toInt()
            val canonY = (tank.position.y - tank.size * Math.sin(Math.PI*angle/180.0)).toInt()
            val velocity = Vec2D(
                pos.copy(),
                Pos2D(canonX.toDouble(), canonY.toDouble())
            ).times(power / 400.0)
            val projectile = Projectile(
                gameScene,
                Pos2D(canonX.toDouble(), canonY.toDouble()),
                velocity,
                0,
                trailColor = Color.BLACK,
                simulated = true,
                onExplode = { position ->
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