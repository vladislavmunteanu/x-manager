package org.xm.core.system;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.xm.core.system.command.Command;
import org.xm.core.system.command.Commands;
import org.xm.core.system.file.XmItem;
import org.xm.core.system.message.RouteFile;
import org.xm.core.system.message.SystemMessage;
import org.xm.core.system.message.XmCommand;


public final class FileRouter extends AbstractBehavior<SystemMessage> {

    private String repositoryPath;
    private ActorRef<SystemMessage> scannerRouter;
    private ActorRef<SystemMessage> fileCommander;
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
                .onMessage(RouteFile.class, this::onRouteFile)
                .build();
    }

    private Behavior<SystemMessage> onRouteFile(SystemMessage message) {
        XmItem fileToRoute = (XmItem) message.getContext();
        fileToRoute.setDestination(resolveFileDestination(fileToRoute));
        Command routeCommand = new Commands.RouteFile(fileToRoute);
        SystemMessage routeFile = new XmCommand(message.getRequestId(), message.from(), fileCommander, routeCommand);
        sendMessageToCommander(routeFile);
        return this;
    }

    private String resolveFileDestination(XmItem xmItem) {
        return String.format("%s/%s", repositoryPath, xmItem.getName()); //TODO implement routing rules
    }

    private void sendMessageToScanner(SystemMessage message) {
        if (scannerRouter == null)
            scannerRouter = routerRegistry.getRouter("file-scanner-pool");
        scannerRouter.tell(message);
    }

    private void sendMessageToCommander(SystemMessage message) {
        if (fileCommander == null)
            fileCommander = routerRegistry.getRouter("file-commander-pool");
        fileCommander.tell(message);
    }

}
