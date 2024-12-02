package Experimental.TerrainScene

import Engine.GameObject2
import Engine.IGameScene
import Engine.Pos2D
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

val WEAPON_QUACK = 0
val WEAPON_TINY_BOMB = 1
val WEAPON_BOMB = 2
val WEAPON_BIGGER_BOMB = 3
val WEAPON_BIGGEST_BOMB = 4
val WEAPON_EARTHQUAKE = 5
val WEAPON_DIRT_BOMB = 6
val WEAPON_MIRV = 7
val WEAPON_NAPALM = 8
val WEAPON_LASER = 9

class Tank(val parent: IGameScene, var rasterTerrain: RasterTerrain, var position: Pos2D, val color: Color) : GameObject2(parent, position) {
    var size = 20
    var power = 100
    var angle = 45.0
    var energy = 100
    val stroke = BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
    var falling: Boolean = false
    var playing = true
    var activeWeapon = WEAPON_BOMB
        set(value) {
            if (value < 0 || value > 9) throw Exception("Weapon must be between 0 and 9 (inclusive)")
            field = value
        }
    var canonX = (position.x + size * Math.cos(Math.PI*angle/180.0)).toInt()
    var canonY = (position.y - size * Math.sin(Math.PI*angle/180.0)).toInt()

    override fun update() {
        if (falling){
            if (position.y.toInt() < rasterTerrain.rasterImage.height-2) {
                if (rasterTerrain.rasterImage.getRGB(position.x.toInt(), position.y.toInt() + 2) == 0 ||
                    rasterTerrain.rasterImage.getRGB(position.x.toInt() - size/4, position.y.toInt() + 2) == 0 ||
                    rasterTerrain.rasterImage.getRGB(position.x.toInt() + size/4, position.y.toInt() + 2) == 0) {
                    position.y += 2
                    rasterTerrain.rasterImage.graphics.color = Color(0,0,0,0)
                    rasterTerrain.pokeLine((position.x-size/2).toInt(), position.y.toInt()-5, (position.x+size/2).toInt(), position.y.toInt()-5, 3f)

                    onTankMoved()
                } else {
                    falling = false
                }
            } else {
                falling = false
            }
        }
    }

    fun setFall(fall: Boolean) {
        this.falling = fall
    }

    override fun draw(g: Graphics2D) {
        g.color = color
        g.stroke = stroke

        g.color = color
        g.drawLine(position.x.toInt(), position.y.toInt(), canonX, canonY)
        g.fillArc((position.x - size/2).toInt(), (position.y - size/2).toInt()+2, size, size, 0, 180)

        drawCenter(g)
    }

    fun drawCenter(g: Graphics2D){
        g.color = Color.WHITE
        g.drawLine(position.x.toInt()-1, position.y.toInt(), position.x.toInt()+1, position.y.toInt())
    }

    fun onTankMoved(){
        updateCanonXY()
    }

    fun onAngleChanged(){
        updateCanonXY()
        println("angle: $angle")
    }

    fun onPowerChanged(){
        println("power: $power")
    }

    // Call this every time the tank has moved, or the angle has changed
    fun updateCanonXY(){
        canonX = (position.x + size * Math.cos(Math.PI*angle/180.0)).toInt()
        canonY = (position.y - size * Math.sin(Math.PI*angle/180.0)).toInt()
        rasterTerrain.pokeLine(position.x.toInt(), position.y.toInt(), canonX, canonY, 3f)
    }

    fun increaseAngle(p: Int){
        angle += p
        if (angle < 0) angle += 180
        else if (angle > 180) angle -= 180

        onAngleChanged()
    }

    fun increasePower(p: Int){
        power += p
        if (power < 0) power = 0
        else if (power > 1000) power = 1000

        onPowerChanged()
    }
}