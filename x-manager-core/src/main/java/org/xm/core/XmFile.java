package org.xm.core;

import org.xm.core.exception.FileAlreadyExistsException;
import org.xm.core.exception.UnsupportedOperationException;
import java.util.List;

public class XmFile implements INode {

    private long id;
    private String name;
    private INode parent;

    public XmFile(INode parent, String name, long id) throws FileAlreadyExistsException, UnsupportedOperationException {
        this.parent = parent;
        this.name = name;
        this.id = id;
        updateParent();
    }

    @Override
    public long id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public INode parent() {
        return this.parent;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public List<INode> children() {
        return null;
    }

    @Override
    public void addChild(INode child) {
    }

    @Override
    public INode getChild(String name) {
        return null;
    }

    @Override
    public String absolutePath() {
        return String.format("%s/%s", this.parent().absolutePath(), this.name());
    }

    @Override
    public void removeChild(INode child) {

    }

    private void updateParent() throws FileAlreadyExistsException, UnsupportedOperationException {
        if (this.parent() != null) {

            if (this.parent.isLeaf())
                throw new UnsupportedOperationException("Parent should be a folder");

            if (this.parent().getChild(this.name) == null){
                this.parent().addChild(this);
            } else {
                throw new FileAlreadyExistsException(String.format("%s already has a file with this name '%s'", this.parent().absolutePath(), this.name()));
            }
        }
    }
}
