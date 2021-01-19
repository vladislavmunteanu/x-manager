package org.xm.core.system.message;

import akka.actor.typed.ActorRef;

public abstract class SystemMessage {

    private final long requestId;
    private final ActorRef<SystemMessage> from;
    private final ActorRef<SystemMessage> to;
    private Object context;

    protected SystemMessage(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to) {
        this.requestId = requestId;
        this.from = from;
        this.to = to;
    }

    protected SystemMessage(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to, Object context) {
        this.requestId = requestId;
        this.from = from;
        this.to = to;
        this.context = context;
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


    public Object getContext() {
        return context;
    }
}
