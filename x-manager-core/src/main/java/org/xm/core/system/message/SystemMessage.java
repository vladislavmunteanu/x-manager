package org.xm.core.system.message;

import akka.actor.typed.ActorRef;

public abstract class SystemMessage {

    private final long requestId;
    private final ActorRef<SystemMessage> from;
    private final ActorRef<SystemMessage> to;

    protected SystemMessage(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to) {
        this.requestId = requestId;
        this.from = from;
        this.to = to;

    }

    public long getRequestId() {
        return this.requestId;
    }

    public ActorRef<SystemMessage> from() {
        return this.from;
    }

    public ActorRef<SystemMessage> to() {
        return this.to;
    }

}
