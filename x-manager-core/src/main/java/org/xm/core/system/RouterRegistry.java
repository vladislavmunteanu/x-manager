package org.xm.core.system;

import akka.actor.typed.ActorRef;
import org.xm.core.system.message.SystemMessage;

import java.util.HashMap;
import java.util.Map;

public class RouterRegistry {

    private Map<String, ActorRef<SystemMessage>> registryMap;
    private static RouterRegistry instance;

    private RouterRegistry() {
        this.registryMap = new HashMap<>();
    }

    synchronized public static RouterRegistry getInstance() {
        if (instance == null)
            instance = new RouterRegistry();

        return instance;
    }

    public void registerRouter(String name, ActorRef<SystemMessage> router) {
        this.registryMap.put(name, router);
    }

    public ActorRef<SystemMessage> getRouter(String name) {
        return this.registryMap.get(name);
    }
}
