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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
        String filePath = (String) message.getContext();
        File fileToMove = new File(filePath);
        Path from = Paths.get(filePath);
        Path to = Paths.get(String.format("%s/%s", repositoryPath, fileToMove.getName()));
        try {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
            SystemMessage fileMoved = new FileMoved(message.getRequestId(), getContext().getSelf(), scannerRouter, message.getContext());
            sendMessage(fileMoved);
        } catch (IOException e) {
            getContext().getLog().error("Failed to move file '{}'", filePath, e);
            e.printStackTrace(); // TODO handle failure
        }
        return this;
    }

    private void sendMessage(SystemMessage message) {
        if (scannerRouter == null)
            scannerRouter = routerRegistry.getRouter("scanner-pool");
        scannerRouter.tell(message);
    }

}
