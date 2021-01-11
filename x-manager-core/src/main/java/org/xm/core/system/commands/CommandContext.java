package org.xm.core.system.commands;

import org.xm.core.INode;

import java.io.InputStream;

public class CommandContext {

    private INode iNode;
    private InputStream data;

    public CommandContext(INode iNode){
        this.iNode = iNode;
    }

    public INode getiNode() {
        return iNode;
    }
}
