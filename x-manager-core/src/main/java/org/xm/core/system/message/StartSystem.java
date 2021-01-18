package org.xm.core.system.message;

import akka.actor.typed.ActorRef;

public final class StartSystem extends SystemMessage {

    public StartSystem(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to) {
        super(requestId, from, to);
    }

}
