package org.xm.core.system;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.xm.core.system.command.Command;
import org.xm.core.system.file.XmItem;
import org.xm.core.system.message.SystemMessage;
import org.xm.core.system.message.XmCommand;

import java.io.IOException;

public class FileCommander extends AbstractBehavior<SystemMessage> {

    private FileCommander(ActorContext<SystemMessage> context) {
        super(context);
        getContext().getLog().debug("New file commander created");
    }

    public static Behavior<SystemMessage> create(String repositoryPath) {
        return Behaviors.setup(FileCommander::new);
    }

    @Override
    public Receive<SystemMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(XmCommand.class, this::executeCommand)
                .build();
    }

    private Behavior<SystemMessage> executeCommand(XmCommand message) {
        Command command = (Command) message.getContext();
        XmItem xmFile = command.getXmItem();
        try {
            getContext().getLog().debug("Executing command {} on {}", command.getCommandName(), xmFile.getLocation());
            command.execute();
            getContext().getLog().debug("Executed command {} on {}", command.getCommandName(), xmFile.getLocation());
        } catch (IOException e) {
            getContext().getLog().error("Failed to execute command {}, on {}", command.getCommandName(), xmFile.getName(), e);
        }
        return this;
    }

}
