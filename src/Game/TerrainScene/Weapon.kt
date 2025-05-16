package Game.TerrainScene

import Engine.IGameScene
import Engine.Pos2D
import Engine.Vec2D
import Game.GameController
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import kotlin.random.Random

abstract class Weapon(
    val id: Int,
    val name: String,
    val purchasePrice: Double,
    val purchaseQuantity: Int,
) {
    companion object {
        val allWeapons: Map<Int, Weapon> = listOf(
            ExplosionWeapon(1, "Knaldperle", 50.0, 50, 20),
            ExplosionWeapon(2, "Kanonslaw", 100.0, 10, 40),
            ExplosionWeapon(3, "Granat", 100.0, 10, 80),
            ExplosionWeapon(4, "Klumpedumpebombe", 120.0, 1, 160),
            EarthquakeWeapon(5, "Jordskælv", 150.0, 2),
            FrogBombWeapon(6, "Frøbombe", 200.0, 3),
            MirvWeapon(7, "MIRV-3", 200.0, 2, 3),
            MirvWeapon(8, "MIRV-5", 300.0, 2, 5),
            MirvWeapon(9, "MIRV-7", 400.0, 2, 7),
        ).associate { it.id to it }
        val minWeaponId = allWeapons.minOf { it.key }
        val maxWeaponId = allWeapons.maxOf { it.key }
    }
    abstract fun drawIcon(g: Graphics2D, x: Int, y: Int)

    open fun getProjectile(gameScene: IGameScene, pos: Pos2D, velocity: Vec2D): Projectile {
        return Projectile(gameScene, pos, velocity, id, GameController.getCurrentPlayer()?.color ?: Color.WHITE)
    }

    abstract fun onExplode(terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile)

}

class MirvWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int, val subProjectiles: Int):
    Weapon(id, name, purchasePrice, purchaseQuantity) {
    override fun drawIcon(g: Graphics2D, x: Int, y: Int) {
        val oldStroke = g.stroke
        g.stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g.drawArc(x + 10, y + 16, 12, 20, -180, -90)

        g.drawArc(x + 6, y + 10, 8, 12, 0, 180)
        g.drawArc(x + 16, y + 10, 8, 12, 180, -180)
        g.drawArc(x + 6, y + 6, 8, 16, 90, -90)
        if (subProjectiles > 3) {
            g.drawArc(x + 16, y + 6, 8, 16, 180, -90)
            g.drawArc(x + 14, y + 16, 8, 8, 90, -90)
        }
        if (subProjectiles > 5) {
            g.drawArc(x + 8, y + 16, 6, 4, 180, -180)
            g.drawArc(x + 14, y + 18, 4, 12, 90, -90)
        }

        g.stroke = oldStroke
    }

    override fun onExplode(
        terrain: RasterTerrain,
        gameScene: IGameScene,
        projectile: Projectile
    ) {
        gameScene.add(Explosion(gameScene, projectile.position, 30, 15, {
            terrain.crumble = true
        }))
        gameScene.remove(projectile)
        GameController.projectilesFlying -= 1
        Projectile.activeProjectiles.remove(projectile)
        for (i in 1 .. subProjectiles) {
            val velocity = Vec2D(Random.nextDouble(-5.0, 5.0), -3.0)
            val p = Projectile(gameScene, projectile.position.copy(), velocity, 3, GameController.getCurrentPlayer()?.color ?: Color.WHITE)
            gameScene.add(p)
        }
    }

    override fun getProjectile(gameScene: IGameScene, pos: Pos2D, velocity: Vec2D): Projectile {
        return MidairExplodingProjectile(gameScene, pos, velocity, id, GameController.getCurrentPlayer()?.color ?: Color.WHITE)
    }

}

class FrogBombWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int):
    Weapon(id, name, purchasePrice, purchaseQuantity) {
    override fun drawIcon(g: Graphics2D, x: Int, y: Int) {
        val oldStroke = g.stroke
        g.stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g.drawArc(x, y+12, 16, 24, 0, 90)
        g.drawArc(x+16, y+16, 8, 16, 0, 180)
        g.stroke = oldStroke
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
            Projectile.activeProjectiles.remove(projectile)
        }
    }
}

class ExplosionWeapon(id: Int, name: String, purchasePrice: Double, purchaseQuantity: Int, val size: Int)
    : Weapon(id, name, purchasePrice, purchaseQuantity) {
    override fun drawIcon(g: Graphics2D, x: Int, y: Int) {
        val iconSize = size/8 + 2
        g.fillOval(x + 16 - iconSize/2, y + 16 - iconSize/2, iconSize, iconSize)
    }

    override fun onExplode(terrain: RasterTerrain, gameScene: IGameScene, projectile: Projectile) {
        gameScene.remove(projectile)
        GameController.projectilesFlying -= 1
        Projectile.activeProjectiles.remove(projectile)
        val exp = Explosion(gameScene, projectile.position, size, 20 + size/3, {
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
        Projectile.activeProjectiles.remove(projectile)
    }
}
