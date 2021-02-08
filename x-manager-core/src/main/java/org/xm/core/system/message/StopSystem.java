package org.xm.core.system.message;

import akka.actor.typed.ActorRef;

public final class StopSystem extends SystemMessage {
    protected StopSystem(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to) {
        super(requestId, from, to);
    }
}
