package Engine.Audio

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object AudioHelper {
    private val clipPlayer = AudioClipPlayer()
    private val _lock = ReentrantLock()

    fun load(path: String, name: String){
        val url = AudioHelper.javaClass.classLoader.getResource(path)
        if (url != null) {
            _lock.withLock {
                clipPlayer.loadSound(url, name)
            }
        }
    }

    fun play(name: String) {
        _lock.withLock {
            clipPlayer.playSound(name)
        }
    }

    fun stop(name: String){
        _lock.withLock {
            clipPlayer.stopSound(name)
        }
    }

    fun loop(name: String, times: Int = -1){
        _lock.withLock {
            clipPlayer.loopSound(name, times)
        }
    }

    fun unload(){
        _lock.withLock {
            clipPlayer.unload()
        }
    }
}