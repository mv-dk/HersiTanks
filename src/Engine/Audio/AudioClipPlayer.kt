package Engine.Audio

import java.net.URL
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class AudioClipPlayer {
    private var audioInputStreamMap = mutableMapOf<String, AudioInputStream>()
    private var clipMap = mutableMapOf<String, Clip>()

    fun loadSound(path: URL, name: String) {
        if (clipMap.containsKey(name)) return

        val audioStream = AudioSystem.getAudioInputStream(path)
        val audioClip = AudioSystem.getClip()
        audioClip.open(audioStream)
        audioInputStreamMap[name] = audioStream
        clipMap[name] = audioClip
    }

    fun playSound(name: String) {
         clipMap[name]?.let { clip ->
             if (clip.isRunning) {
                 clip.stop()
                 clip.flush()
             }
             clip.framePosition = 0
             while (!clip.isRunning)
                 clip.start()

         }
    }

    fun stopSound(name: String) {
        clipMap[name]?.let { clip ->
            while (clip.isRunning) {
                clip.stop()
                clip.flush()
                clip.framePosition = 0
            }
        }
    }

    fun loopSound(name: String, times: Int = -1){
        clipMap[name]?.loop(times)
    }

    fun unload(){
        for (clip in clipMap){
            clip.value.close()
        }
        for (audioStream in audioInputStreamMap){
            audioStream.value.close()
        }
    }
}