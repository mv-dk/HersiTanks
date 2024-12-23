package Experimental.TerrainScene

import Engine.IGameScene
import Game.GameController
import java.awt.Graphics2D

abstract class Weapon(
    val id: Int,
    val  name: String,
    purchasePrice: Double,
    purchaseQuantity: Int,
    val onExplode: (terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile) -> Unit
) {
    companion object {
        val allWeapons = listOf(
            ExplosionWeapon(1, "Knaldperle", 10.0, 50, 10),
            ExplosionWeapon(2, "Kanonslaw", 50.0, 25, 20),
            ExplosionWeapon(3, "Granat", 100.0, 10, 40),
            ExplosionWeapon(4, "Klumpedumpebombe", 200.0, 5, 80),
            EarthquakeWeapon(5, "Jordskælv", 40.0, 2)
        )
    }
    abstract fun drawIcon(g: Graphics2D, x: Int, y: Int)

}

class ExplosionWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int, val size: Int)
    : Weapon(id, name, purchasePrice, purchaseQuantity, bombExplosion(size)) {
    override fun drawIcon(g: Graphics2D, x: Int, y: Int) {
        val iconSize = size/4 + 2
        g.fillOval(x + 16 - iconSize/2, y + 16 - iconSize/2, iconSize, iconSize)
    }
}

class EarthquakeWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int)
    : Weapon(id, name, purchasePrice, purchaseQuantity, earthquake()) {
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

}

fun bombExplosion(size: Int) : (terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile) -> Unit {
    return {terrain, gameScene, projectile ->
        gameScene.remove(projectile)
        GameController.projectilesFlying -= 1
        val exp = Explosion(gameScene, projectile.position, size, size / 2, {
            terrain.crumble = true
        })
        gameScene.add(exp)
    }
}

fun earthquake() : (terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile) -> Unit  {
    return {terrain, gameScene, projectile ->
        terrain.startEarthquake(projectile.position.x.toInt(), projectile.position.y.toInt())
        gameScene.remove(projectile)
        GameController.projectilesFlying -= 1
    }
}
