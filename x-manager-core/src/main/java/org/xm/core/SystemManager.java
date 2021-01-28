package org.xm.core;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.*;
import org.xm.core.system.FileRouter;
import org.xm.core.system.FileScanner;
import org.xm.core.system.RouterRegistry;
import org.xm.core.system.message.StartSystem;
import org.xm.core.system.message.SystemMessage;

public final class SystemManager extends AbstractBehavior<SystemMessage> {

    private final Configuration conf;
    private final RouterRegistry routerRegistry;
    private Boolean systemStarted;

    private SystemManager(ActorContext<SystemMessage> context, Configuration conf) {
        super(context);
        this.conf = conf;
        this.systemStarted = false;
        this.routerRegistry = RouterRegistry.getInstance();
        getContext().getLog().info("System manager initialized");
    }

    public static Behavior<SystemMessage> create(Configuration conf) {
        return Behaviors.setup(context -> new SystemManager(context, conf));
    }

    @Override
    public Receive<SystemMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartSystem.class, this::onSystemStart)
                .build();
    }

    private Behavior<SystemMessage> onSystemStart(SystemMessage message) {
        if (!systemStarted) {
            int poolSize = 2;
            PoolRouter<SystemMessage> scannerPool = Routers.pool(poolSize, Behaviors.supervise(FileScanner.create(conf.getConnectorPath())).onFailure(SupervisorStrategy.restart())).withRoundRobinRouting();
            PoolRouter<SystemMessage> fileRouterPool = Routers.pool(poolSize, Behaviors.supervise(FileRouter.create(conf.getRepositoryPath())).onFailure(SupervisorStrategy.restart())).withRoundRobinRouting();
            ActorRef<SystemMessage> scannerRouter = getContext().spawn(scannerPool, "scanner-pool");
            ActorRef<SystemMessage> fileRouter = getContext().spawn(fileRouterPool, "file-router-pool");
            routerRegistry.registerRouter("scanner-pool", scannerRouter);
            routerRegistry.registerRouter("file-router-pool", fileRouter);
            systemStarted = true;
        }
        getContext().getLog().info("System is up and running");
        return this;
    }
}
