package Experimental.TerrainScene

import Engine.IGameScene
import Engine.Pos2D
import Engine.Vec2D
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

abstract class Weapon(
    val id: Int,
    val name: String,
    val purchasePrice: Double,
    val purchaseQuantity: Int,
) {
    companion object {
        val allWeapons: Map<Int, Weapon> = listOf(
            ExplosionWeapon(1, "Knaldperle", 100.0, 50, 10),
            ExplosionWeapon(2, "Kanonslaw", 200.0, 10, 20),
            ExplosionWeapon(3, "Granat", 300.0, 10, 40),
            ExplosionWeapon(4, "Klumpedumpebombe", 1000.0, 5, 80),
            EarthquakeWeapon(5, "Jordskælv", 450.0, 2),
            FrogBombWeapon(6, "Frøbombe", 1100.0, 3)
        ).associate { it.id to it }
        val minWeaponId = allWeapons.minOf { it.key }
        val maxWeaponId = allWeapons.maxOf { it.key }
    }
    abstract fun drawIcon(g: Graphics2D, x: Int, y: Int)

    open fun getProjectile(gameScene: IGameScene, pos: Pos2D, velocity: Vec2D): Projectile {
        return Projectile(gameScene, pos, velocity, id)
    }

    abstract fun onExplode(terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile)

}

class FrogBombWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int):
    Weapon(id, name, purchasePrice, purchaseQuantity) {
    override fun drawIcon(g: Graphics2D, x: Int, y: Int) {
        g.stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g.drawArc(x, y+12, 16, 24, 0, 90)
        g.drawArc(x+16, y+16, 8, 16, 0, 180)
    }

    override fun onExplode(terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile) {
        projectile.jumps += 1
        val possibleNextVelocity = Vec2D(projectile.velocity.x, -projectile.velocity.y)
        val nextPossiblePos = projectile.position.plus(possibleNextVelocity)
        if (nextPossiblePos.x >= 0 && nextPossiblePos.x < terrain.rasterImage.width && nextPossiblePos.y < terrain.rasterImage.height) {
            if (terrain.rasterImage.getRGB(nextPossiblePos.x.toInt(), nextPossiblePos.y.toInt()) != 0) {
                projectile.velocity.x *= -0.5
                projectile.velocity.y *= -0.5
            } else {
                projectile.velocity.y *= -0.5
            }
        }

        val exp = Explosion(gameScene, projectile.position, 30, 15, {
            terrain.crumble = true
        })
        gameScene.add(exp)

        if (projectile.jumps >= 3) {
            gameScene.remove(projectile)
            GameController.projectilesFlying -= 1
        }
    }
}

class ExplosionWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int, val size: Int)
    : Weapon(id, name, purchasePrice, purchaseQuantity) {
    override fun drawIcon(g: Graphics2D, x: Int, y: Int) {
        val iconSize = size/4 + 2
        g.fillOval(x + 16 - iconSize/2, y + 16 - iconSize/2, iconSize, iconSize)
    }

    override fun onExplode(terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile) {
        gameScene.remove(projectile)
        GameController.projectilesFlying -= 1
        val exp = Explosion(gameScene, projectile.position, size, size / 2, {
            terrain.crumble = true
        })
        gameScene.add(exp)
    }
}

class EarthquakeWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int)
    : Weapon(id, name, purchasePrice, purchaseQuantity) {
    override fun drawIcon(g: Graphics2D, x: Int, y: Int) {
        g.fillOval(x + 10, y + 10, 3, 3)
        g.fillOval(x + 15, y + 10, 3, 3)
        g.fillOval(x + 20, y + 10, 3, 3)
        g.fillOval(x + 9, y + 15, 3, 3)
        g.fillOval(x + 14, y + 15, 3, 3)
        g.fillOval(x + 19, y + 15, 3, 3)
        g.fillOval(x + 10, y + 20, 3, 3)
        g.fillOval(x + 15, y + 20, 3, 3)
        g.fillOval(x + 20, y + 20, 3, 3)
    }

    override fun onExplode(terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile) {
        terrain.startEarthquake(projectile.position.x.toInt(), projectile.position.y.toInt())
        gameScene.remove(projectile)
        GameController.projectilesFlying -= 1
    }
}
