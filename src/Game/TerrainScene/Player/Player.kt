package Game.TerrainScene.Player

import Engine.AudioHelper
import Engine.Pos2D
import Game.GameController
import Game.TerrainScene.Tank
import Game.TerrainScene.Weapon
import SND_CLICK
import SND_FIRE
import SND_FIRE2
import SND_FIRE3
import SND_FIZZLE
import SND_SWOOSH
import SND_SWOOSH2
import java.awt.Color
import kotlin.random.Random

class Player(var name: String, val playerType: PlayerType) {
    var tank: Tank? = null
    var playing: Boolean = true
    var weaponry = mutableMapOf<Int,Int>() // Map from weaponId to ammo
    var fuel = 0.0
    var money = 200.0
    var color = Color.RED
    var currentWeaponId = 1

    fun victories(): Int {
        return GameController.teams.find { it.players.contains(this) }?.victories ?: 0
    }

    fun decreaseAmmoAndCycleIfZero() {
        weaponry[currentWeaponId] = (weaponry[currentWeaponId] ?: 1) -1
        if ((weaponry[currentWeaponId] ?: 0) == 0) {
            cycleWeapon()
        }
    }

    fun cycleWeapon() {
        val oldWeaponId = currentWeaponId
        if ((tank?.id ?: 0) % 3 == 0) AudioHelper.play(SND_CLICK)
        else if ((tank?.id ?: 0) % 2 == 0) AudioHelper.play(SND_SWOOSH)
        else AudioHelper.play(SND_SWOOSH2)

        while (true) {
            currentWeaponId += 1
            if (currentWeaponId > Weapon.maxWeaponId) currentWeaponId = Weapon.minWeaponId
            if (weaponry.containsKey(currentWeaponId) && (weaponry[currentWeaponId] ?: 0) > 0) break
            if (currentWeaponId == oldWeaponId) break
        }
    }

    fun fire() {
        when (Random.nextInt(3)) {
            0 -> AudioHelper.play(SND_FIRE)
            1 -> AudioHelper.play(SND_FIRE2)
            2 -> AudioHelper.play(SND_FIRE3)
        }

        GameController.getCurrentPlayer()?.let { player ->
            if ((player.weaponry[player.currentWeaponId] ?: 0) == 0) {
                AudioHelper.play(SND_FIZZLE);
            } else {
                tank?.let {
                    val velocity = it.getFireVelocity()
                    val position = Pos2D(it.canonX.toDouble(), it.canonY.toDouble())
                    val projectile =
                        Weapon.allWeapons[player.currentWeaponId]?.getProjectile(it.parent, position, velocity)
                    if (projectile != null) {
                        it.parent.add(projectile)
                    }
                    player.decreaseAmmoAndCycleIfZero()
                }
            }
        }
    }
}