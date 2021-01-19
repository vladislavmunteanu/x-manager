package org.xm.core.system.message;

import akka.actor.typed.ActorRef;

public final class MoveFile extends SystemMessage {
    public MoveFile(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to, Object context) {
        super(requestId, from, to, context);
    }
}
