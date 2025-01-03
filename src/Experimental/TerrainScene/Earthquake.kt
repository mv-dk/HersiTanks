package Experimental.TerrainScene

import Engine.GameRunner
import Engine.Vec2D

class Earthquake(val x: Int, val y: Int, numCracks: Int, val size: Int, val branchProbability: Double){
    var cracks = mutableListOf<EarthquakeCrack>()

    init {
        for (i in 0..numCracks-1){
            cracks.add(createCrack(x.toDouble(), y.toDouble(), size))
        }
    }

    fun createCrack(x: Double, y: Double, growDuration: Int): EarthquakeCrack {
        return EarthquakeCrack(
            x,
            y,
            Vec2D(random.nextDouble(-5.0, 5.0), random.nextDouble(-5.0, 5.0)),
            growDuration
        )
    }

    fun update(terrain: RasterTerrain): Boolean {
        var didChangeTerrain = false
        val newCracks = mutableListOf<EarthquakeCrack>()
        for (crack in cracks){
            if (crack.growDuration > 0) {
                crack.growDuration -= 1

                val holeSize = Math.min(4, crack.growDuration/10)
                val oldX = crack.x
                val oldY = crack.y

                if (random.nextDouble() < 0.1 || crack.growDuration % 6 == 0){
                    crack.x -= crack.direction.y * 60.0/GameRunner.fps
                    crack.y += crack.direction.x * 60.0/GameRunner.fps
                } else {
                    crack.x += crack.direction.x * 60.0/GameRunner.fps
                    crack.y += crack.direction.y * 60.0/GameRunner.fps
                    val futureX = crack.x + crack.direction.x * 10.0
                    val futureY = crack.y + crack.direction.y * 10.0
                    if (futureX < 0 || futureX > terrain.rasterImage.width || futureY < 0 || futureY > terrain.rasterImage.height ||
                        terrain.rasterImage.getRGB(futureX.toInt(), futureY.toInt()) == 0){
                        crack.growDuration = 0
                        continue
                    }
                }
                terrain.pokeLine(oldX.toInt(), oldY.toInt(), crack.x.toInt(), crack.y.toInt(), holeSize.toFloat())
                if (random.nextDouble() < 0.9) {
                    terrain.pokeLine(
                        oldX.toInt(),
                        oldY.toInt(),
                        crack.x.toInt() - 10 + random.nextInt(20),
                        crack.y.toInt() - 10 + random.nextInt(20),
                        2f
                    )
                }
                if (random.nextDouble() < branchProbability/30){
                    if (random.nextDouble() < 0.1){
                        newCracks.add(createCrack(crack.x, crack.y, size))
                    } else {
                        newCracks.add(createCrack(crack.x, crack.y, (crack.growDuration * 1.0).toInt()))
                    }
                }

                crack.direction.x += random.nextDouble(-0.10, 0.10)
                crack.direction.y += random.nextDouble(-0.10, 0.10)
                didChangeTerrain = true
            }
        }
        cracks.addAll(newCracks)

        return didChangeTerrain
    }

    fun remove(){
        cracks.clear()
    }
}

class EarthquakeCrack(var x: Double, var y: Double, val direction: Vec2D, var growDuration: Int)