package org.xm.core.system;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.xm.core.system.message.StartSystem;
import org.xm.core.system.message.SystemMessage;

public final class FileScanner extends AbstractBehavior<SystemMessage> {

    private String connectorPath;
    private ActorRef<SystemMessage> fileRouter;
    private RouterRegistry routerRegistry;

    private FileScanner(ActorContext<SystemMessage> context, String connectorPath) {
        super(context);
        this.connectorPath = connectorPath;
        this.routerRegistry = RouterRegistry.getInstance();
    }

    public static Behavior<SystemMessage> create(String connectorPath) {
        return Behaviors.setup(context -> new FileScanner(context, connectorPath));
    }

    @Override
    public Receive<SystemMessage> createReceive() {
        return newReceiveBuilder().build();
    }

    private void sendMessage(SystemMessage message) {
        if (fileRouter == null)
            fileRouter = routerRegistry.getRouter("file-router-pool");
        fileRouter.tell(message);
    }
}
