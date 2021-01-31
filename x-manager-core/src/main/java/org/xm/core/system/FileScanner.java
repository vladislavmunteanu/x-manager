package org.xm.core.system;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.xm.core.system.file.XmItem;
import org.xm.core.system.message.RouteFile;
import org.xm.core.system.message.SystemMessage;
import org.xm.core.system.worker.ScannerWorker;

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
                .onMessage(RouteFile.class, this::onRouteFile)
                .build();
    }

    private Behavior<SystemMessage> onRouteFile(SystemMessage message) {
        SystemMessage routeFileMessage = new RouteFile(message.getRequestId(), getContext().getSelf(), fileRouter, (XmItem) message.getContext());
        sendMessage(routeFileMessage);
        return this;
    }


    private void sendMessage(SystemMessage message) {
        if (fileRouter == null)
            fileRouter = routerRegistry.getRouter("file-router-pool");
        fileRouter.tell(message);
    }
}
