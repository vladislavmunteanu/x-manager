package org.xm.core.system.message;

import akka.actor.typed.ActorRef;
import org.xm.core.system.command.Command;

public final class XmCommand extends SystemMessage {

    public XmCommand(long requestId, ActorRef<SystemMessage> from, ActorRef<SystemMessage> to, Command context) {
        super(requestId, from, to, context);
    }
}
