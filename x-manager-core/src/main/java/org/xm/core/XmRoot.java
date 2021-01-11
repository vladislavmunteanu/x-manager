package org.xm.core;

import org.xm.core.exception.FileAlreadyExistsException;
import org.xm.core.exception.FolderAlreadyExistsException;

public class XmRoot extends XmFolder {

    private static INode rootInstance;

    private XmRoot(INode parent, String name, long id) throws FileAlreadyExistsException, FolderAlreadyExistsException {
        super(parent, name, id);
    }

    public static synchronized INode getRootInstance(String path) throws FileAlreadyExistsException, FolderAlreadyExistsException {
        if (rootInstance == null)
            rootInstance = new XmRoot(null, "root", 0);
        return rootInstance;
    }

    @Override
    public String absolutePath(){
        return "";
    }
}
