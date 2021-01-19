package org.xm.core.system.message;

import akka.actor.typed.ActorRef;

public final class FileMoved extends SystemMessage{
    public FileMoved(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to, Object context) {
        super(requestId, from, to, context);
    }
}
