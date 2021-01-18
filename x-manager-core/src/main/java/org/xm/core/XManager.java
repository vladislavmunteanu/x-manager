package org.xm.core;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import org.xm.core.system.message.StartSystem;
import org.xm.core.system.message.SystemMessage;

public class XManager {
    private final ActorRef<SystemMessage> systemManager;
    private Configuration conf;
    private static XManager instance;

    private XManager(Configuration conf) {
        this.conf = conf;
        this.systemManager = ActorSystem.create(SystemManager.create(conf), "system-manager");
        this.systemManager.tell(new StartSystem(0, systemManager, systemManager));
    }

    synchronized public static XManager getInstance(Configuration conf) {
        if (instance == null)
            instance = new XManager(conf);
        return instance;
    }

//    public static void main(String[] args){
//        Configuration conf = new Configuration();
//        XManager xManager = new XManager(conf);
//    }

}
