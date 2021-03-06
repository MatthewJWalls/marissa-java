package org.marissa;

import co.paralleluniverse.fibers.SuspendExecution;
import java.util.ArrayList;
import org.marissa.lib.Marissa;
import org.marissa.lib.Persist;
import org.marissa.lib.Router;
import org.marissa.modules.*;
import org.marissa.modules.define.Define;
import org.marissa.modules.scripting.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.xmpp.core.XmppException;


public class Main {
    
    public static void main(String[] args) throws XmppException, SuspendExecution, InterruptedException {

        String username = Persist.load("core", "userid"); 
        String password = Persist.load("core", "password"); 
        String nickname = Persist.load("core", "nickname"); 
        final String joinRoom = Persist.load("core", "joinroom"); 

        Router router = new Router( "(?i)@?"+nickname );

        //no cats
        //router.whenContains(".*cat.*", new Cat()::routingEvent);

        router.on(".*time.*", MiscUtils::tellTheTime);
        router.on("selfie", MiscUtils::selfie);
        router.on("ping", MiscUtils::ping);
        router.on("echo.*", MiscUtils::echo);

        router.on("define\\s+.*", Define::defineWord);

        router.on("(search|image)\\s+.*", Search::search);
        router.on("animate\\s+.*", Animate::search);

        router.on("[-+]\\d+", Score::scoreChange);
        router.on("score", Score::scores);
        router.whenContains("[-+]\\d+\\s+(?i)@?"+nickname,
                            (trigger,response) -> {
                                String noNick = trigger.replaceAll("(?i)@?"+nickname, "");
                                Score.scoreChange(noNick, response);
                            });


        // TODO we can do something with this later
        // router.on(".*", ScriptEngine::all);

        Marissa marissa = new Marissa(username, password, nickname);

        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                 @Override
                                                 public void run() {
                                                     Logger l = LoggerFactory.getLogger(Main.class);
                                                     l.debug("Shutdown hook triggered");
                                                     marissa.onQuit();
                                                     l.debug("Shutdown hook completed");
                                                 }
                                             });

        LoggerFactory.getLogger(Main.class).info("Launching...");

        marissa.connect()
               .activate(new ArrayList<String>() {{ add(joinRoom); }},
                         router);

    }
    
}
