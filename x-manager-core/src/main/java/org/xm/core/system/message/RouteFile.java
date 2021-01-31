package org.xm.core.system.message;

import akka.actor.typed.ActorRef;
import org.xm.core.system.file.XmItem;

public final class RouteFile extends SystemMessage {
    public RouteFile(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to, XmItem context) {
        super(requestId, from, to, context);
    }
}
