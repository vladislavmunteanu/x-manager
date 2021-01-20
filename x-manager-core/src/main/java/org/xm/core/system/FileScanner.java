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
import org.xm.core.system.worker.ScannerWorker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileScanner extends AbstractBehavior<SystemMessage> {

    private String connectorPath;
    private ActorRef<SystemMessage> fileRouter;
    private RouterRegistry routerRegistry;

    private FileScanner(ActorContext<SystemMessage> context, String connectorPath) {
        super(context);
        this.connectorPath = connectorPath;
        this.routerRegistry = RouterRegistry.getInstance();
        _init();
        getContext().getLog().debug("New file scanner created");
    }

    private void _init() {
        ScannerWorker scannerWorker = new ScannerWorker(connectorPath, getContext().getSelf());
        Thread scanThread = new Thread(scannerWorker);
        scanThread.start();
    }

    public static Behavior<SystemMessage> create(String connectorPath) {
        return Behaviors.setup(context -> new FileScanner(context, connectorPath));
    }

    @Override
    public Receive<SystemMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(MoveFile.class, this::onMoveFile)
                .onMessage(FileMoved.class, this::onFileMoved)
                .build();
    }

    private Behavior<SystemMessage> onMoveFile(SystemMessage message) {
        SystemMessage moveFile = new MoveFile(message.getRequestId(), getContext().getSelf(), fileRouter, message.getContext());
        sendMessage(moveFile);
        return this;
    }

    private Behavior<SystemMessage> onFileMoved(SystemMessage message) {
        String processedFilePath = (String) message.getContext();
        Path lockedFile = Paths.get(String.format("%s.lock", processedFilePath));
        try {
            Files.delete(lockedFile);
            getContext().getLog().debug("File '{}' was successfully moved");
        } catch (IOException e) {
            getContext().getLog().error("Failed to delete file '{}'", lockedFile.toString(), e);
        }
        return this;
    }

    private void sendMessage(SystemMessage message) {
        if (fileRouter == null)
            fileRouter = routerRegistry.getRouter("file-router-pool");
        fileRouter.tell(message);
    }
}
