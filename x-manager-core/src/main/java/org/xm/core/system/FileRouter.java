package org.xm.core.system;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.xm.core.system.message.StartSystem;
import org.xm.core.system.message.SystemMessage;

public final class FileRouter extends AbstractBehavior<SystemMessage> {

    private String repositoryPath;
    private ActorRef<SystemMessage> scannerRouter;
    private RouterRegistry routerRegistry;

    private FileRouter(ActorContext<SystemMessage> context, String repositoryPath) {
        super(context);
        this.repositoryPath = repositoryPath;
        this.routerRegistry = RouterRegistry.getInstance();
    }

    public static Behavior<SystemMessage> create(String repositoryPath) {
        return Behaviors.setup(context -> new FileRouter(context, repositoryPath));
    }

    @Override
    public Receive<SystemMessage> createReceive() {
        return newReceiveBuilder().build();
    }

}
