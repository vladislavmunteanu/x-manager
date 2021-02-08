package org.xm.core;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xm.core.system.message.StartSystem;
import org.xm.core.system.message.SystemMessage;
import sun.misc.Contended;

@Component
public class XManager {
    private Configuration conf;
    private static XManager instance;

    @Autowired
    private XManager(Configuration conf) {
        this.conf = conf;
        ActorRef<SystemMessage> systemManager = ActorSystem.create(SystemManager.create(conf), "system-manager");
        systemManager.tell(new StartSystem(0, systemManager, systemManager));
    }

    synchronized public static XManager getInstance(Configuration conf) {
        if (instance == null)
            instance = new XManager(conf);
        return instance;
    }

    public Configuration getConf() {
        return conf;
    }

    public static void main(String[] args) {
        Configuration conf = new Configuration();
        XManager xManager = new XManager(conf);
    }

}
