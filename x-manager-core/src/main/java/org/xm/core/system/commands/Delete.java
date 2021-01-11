package org.xm.core.system.commands;

import org.xm.core.INode;

import java.io.File;

public class Delete implements Command {

    private INode iNode;

    @Override
    public void execute(CommandContext context) {
        _init(context);

    }

    private void _init(CommandContext context) {
        this.iNode = context.getiNode();
    }

    private void physicalDelete(){
        String path = this.iNode.absolutePath();

    }

    private void logicalDelete() {
        INode parent = this.iNode.parent();
        parent.removeChild(this.iNode);
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
