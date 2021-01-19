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

public final class FileScanner extends AbstractBehavior<SystemMessage> {

    private String connectorPath;
    private ActorRef<SystemMessage> fileRouter;
    private RouterRegistry routerRegistry;
    private ScannerWorker scannerWorker;

    private FileScanner(ActorContext<SystemMessage> context, String connectorPath) {
        super(context);
        this.connectorPath = connectorPath;
        this.routerRegistry = RouterRegistry.getInstance();
        this.scannerWorker = new ScannerWorker(connectorPath, getContext().getSelf());
        Thread scanThread = new Thread(this.scannerWorker);
        scanThread.start();
        getContext().getLog().debug("New file scanner created");
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
        System.out.println(message.getContext().toString()+" file removed");
        return this;
    }

    private void sendMessage(SystemMessage message) {
        if (fileRouter == null)
            fileRouter = routerRegistry.getRouter("file-router-pool");
        fileRouter.tell(message);
    }
}
