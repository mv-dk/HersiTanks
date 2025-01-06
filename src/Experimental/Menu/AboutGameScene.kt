package Experimental.Menu

import Engine.GameScene
import gameResX
import gameResY
import gameWindow
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import kotlin.math.min

class AboutGameScene: GameScene(Color(229, 204, 202), gameResX, gameResY) {
    private val font: Font = Font(Font.DIALOG, Font.PLAIN, 12)
    private val textColor = Color(49, 24, 22)
    var scrollIdx = 0
    var text = """
    HersiTanks is a game made for Hersi, who LOVES the good old DOS classic, 
    Tank Wars. 
    
    
    
    
    
    
    Scroll down to read my testimony of faith, and how to find eternal life.
      ↓
     
      
    
    
    = Introduction =
    According to the Bible, there is a God who has created you, me, and
    everything in this universe. Just like I have created this tiny game,
    with its quirky rules and systems. And there would be no way for the
    actors inside this game to prove that I exist - even if the actors
    were simulated down to the subatomic particles.
    
    = Harmony =
    The Bible tells a story of how God created the world to be a place
    of harmony, where humans were supposed to live peacefully together,
    and where God had an intimate friendship with His creation.
    God had given us a perfect world, where the nature demonstrates His
    genius, His creativity, power, sensitivity, even His humor...!
    Truly, God had done everything He could to give Man the most
    wonderful universe to explore. 
    
    = Rebellion =
    But man soon rebelled against our God, in spite of everything he was
    given. Man chose to distrust God. The creation rebelled against its
    creator - funny enough, that's a reoccurring theme in many stories
    of the present day. But in the Bible it is different, because God
    already from day 1 foretold that He was going to redeem humanity,
    and reconcile the fallen human with himself. 
    
    = Righteousness =
    According to the Bible, God longs to be together with us humans,
    but because we are all lawbreakers, we cannot come near the King. 
    And because God is a righteous judge, we are all going to be judged
    based on the life we have lived.
     
    In a court of law, a crime is rewarded with a penalty. If the judge is 
    righteous, a crime must be punished, even if that was the only crime
    in the person's whole life. 
    
    God is also a righteous judge. If we have committed even one sin,
    we are lawbreakers - sinners who cannot come near God. For every sin, 
    we have to pay a penalty. But we know that a crime against a higher 
    authority brings a greater punishment. Unfortunately for us, since
    we have sinned against an eternal authority, a righteous punishment 
    would be eternal.
    
    = Payment =
    Now, if the judge orders you to pay a fine of $100.000, and your
    rich uncle gives you $100.000 to pay with, how much is left for you
    to pay? Exactly, nothing.
    
    The Bible tells how God sent His only son, Jesus Christ, to Earth.
    He came to pay the penalty for our sin. He lived a perfect life, 
    without sinning, and therefore without earning any penalties 
    for himself. And because of that, He was able to pay the penalty 
    for all humans once and for all, thereby reconciling Man with our
    Creator.
    
    Therefore, according to the Bible, it possible to win eternal 
    life by accepting the payment that Jesus Christ offers to pay.
    
       "For God so loved the world, 
        that He gave his only begotten son, 
        that whoever believes in him 
        should not perish, 
        but have eternal life" 
      - The Gospel of John, chapter 3, verse 16
    
    = What now? =
    You should investigate the claim that Jesus rose from the dead.
    The first followers of Jesus were willing to die for the claim
    that they saw Him alive. I have never heard of anybody who would
    willingly die for something they knew was a lie. To me, that 
    makes a strong case for the story to actually be true. Further
    more, since the time where I have started to put my faith into
    practice, I have experienced several supernatural answers to 
    prayers that can best be explained by God's intervention. 
    
    = Recommendation =
    My recommendation for you: Ask God if He is there. Ask Him to 
    give you a sign if He exists. That's all.
    
    You are welcome to contact me, if you have any questions or
    comments.
    Email: mvestergaard@gmail.com
    
    May you have a wonderful life, and may our Creator provide
    for you abundantly.
    
    Best wishes,
    Martin Vestergaard
    
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    """.trimIndent().split("\n")

    override fun load() {

    }

    override fun draw(g: Graphics2D) {
        super.draw(g)
        g.color = textColor
        g.font = font
        for ((index, line) in text.subList(scrollIdx, min(scrollIdx+18, text.size)).withIndex()) {
            g.drawString(line, 20, 20 * (1+index))
        }
    }

    override fun keyPressed(e: KeyEvent) {
        super.keyPressed(e)
        when (e.keyCode) {
            KeyEvent.VK_ENTER,
            KeyEvent.VK_ESCAPE -> {
                unload()
                gameWindow?.gameRunner?.currentGameScene = MenuGameScene()
            }
            KeyEvent.VK_DOWN -> {
                scrollDown()
            }
            KeyEvent.VK_UP -> {
                scrollUp()
            }
            KeyEvent.VK_SPACE -> {
                scrollDown(10)
            }
        }
    }

    private fun scrollDown(lines: Int = 1) {
        scrollIdx += lines
        if (scrollIdx >= text.size -1) scrollIdx = text.size -1
    }

    private fun scrollUp() {
        if (scrollIdx > 0) scrollIdx -= 1
    }
}