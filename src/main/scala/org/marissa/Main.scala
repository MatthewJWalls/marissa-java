package org.marissa.client

import org.marissa.core.Marissa

/**
  * Created by mjw on 06/08/2016.
  */
object Main extends App {

  // connection details

  val details = new ConnectionDetails(
    user = "test",
    pass = "test",
    nick = "test",
    rooms = List("test")
  )

  // create the bot

  val bot = Marissa(details)

  // attach some example handlers

  bot.handler((tx, rx) => {
    while(true) {
      val msg = rx.take()
      if(msg.body.toLowerCase contains "are you alive") {
        tx.add(ChatMessage(msg.from, "Alive on hook 1"))
      }
    }
  })

  bot.handler((tx, rx) => {
    while(true) {
      val msg = rx.take()
      if(msg.body.toLowerCase contains "are you alive") {
        tx.add(ChatMessage(msg.from, "alive on hook 2"))
      }
    }
  })

  // start (this will block)

  bot.start()

}
