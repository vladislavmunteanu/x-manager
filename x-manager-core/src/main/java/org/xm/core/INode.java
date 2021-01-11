package org.xm.core;

import java.util.List;

public interface INode {

    long id();

    String name();

    INode parent();

    boolean isLeaf();

    List<INode> children();

    void addChild(INode child);

    INode getChild(String name);

    String absolutePath();

    void removeChild(INode child);
}
