package org.xm.core.system;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.xm.core.system.message.FileMoved;
import org.xm.core.system.message.MoveFile;
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
        getContext().getLog().debug("New file router created");
    }

    public static Behavior<SystemMessage> create(String repositoryPath) {
        return Behaviors.setup(context -> new FileRouter(context, repositoryPath));
    }

    @Override
    public Receive<SystemMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(MoveFile.class, this::onMoveFile)
                .build();
    }

    private Behavior<SystemMessage> onMoveFile(SystemMessage message) {
        System.out.println(message.getContext().toString() + " file routed");
        SystemMessage fileMoved = new FileMoved(message.getRequestId(), getContext().getSelf(), scannerRouter, message.getContext());
        sendMessage(fileMoved);
        return this;
    }

    private void sendMessage(SystemMessage message) {
        if (scannerRouter == null)
            scannerRouter = routerRegistry.getRouter("scanner-pool");
        scannerRouter.tell(message);
    }

}
