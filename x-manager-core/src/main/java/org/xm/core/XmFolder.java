package org.xm.core;

import org.xm.core.exception.FileAlreadyExistsException;
import org.xm.core.exception.FolderAlreadyExistsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmFolder implements INode {

    private long id;
    private String name;
    private INode parent;
    private List<INode> children;
    private Map<String, INode> nodeMap;

    public XmFolder(INode parent, String name, long id) throws FileAlreadyExistsException, FolderAlreadyExistsException {
        this.name = name;
        this.id = id;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.nodeMap = new HashMap<>();
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
        return false;
    }

    @Override
    public List<INode> children() {
        return this.children;
    }

    @Override
    public void addChild(INode child) {
        this.nodeMap.put(child.name(), child);
        this.children().add(child);
    }

    @Override
    public INode getChild(String name) {
        return this.nodeMap.get(name);
    }

    @Override
    public String absolutePath() {
        return String.format("%s/%s", this.parent().absolutePath(), this.name());
    }

    @Override
    public void removeChild(INode child) {
        this.nodeMap.remove(child.name());
        children.remove(child);
    }

    private void updateParent() throws FileAlreadyExistsException, FolderAlreadyExistsException {
        if (this.parent() != null) {
            if (this.parent().getChild(this.name) == null) {
                this.parent().addChild(this);
            } else {
                if (this.isLeaf())
                    throw new FileAlreadyExistsException(String.format("%s already has a file with this name '%s'", this.parent().absolutePath(), this.name()));
                else
                    throw new FolderAlreadyExistsException(String.format("%s already has a folder with this name '%s'", this.parent().absolutePath(), this.name()));
            }
        }
    }
}
