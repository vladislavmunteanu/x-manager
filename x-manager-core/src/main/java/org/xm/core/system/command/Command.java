package org.xm.core.system.command;

import org.xm.core.system.file.XmItem;

import java.io.IOException;

public abstract class Command {

    protected String commandName;
    protected XmItem xmItem;

    public Command(String commandName, XmItem xmItem) {
        this.commandName = commandName;
        this.xmItem = xmItem;
    }

    public abstract void execute() throws IOException;

    public String getCommandName(){
        return this.commandName;
    }

    public XmItem getXmItem(){
        return this.xmItem;
    }

}
