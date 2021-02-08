package org.xm.core.system.file;

import org.xm.core.exception.UnsupportedOperationException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class XmItem {

    protected Path sourcePath;
    protected Path targetPath;
    protected String location;
    protected String destination;
    protected String name;

    public XmItem(String location) {
        this.location = location;
        this.sourcePath = Paths.get(this.getLocation());
        this.name = sourcePath.getFileName().toString();
    }

    public abstract Boolean isFile();

    public abstract void copy() throws IOException, UnsupportedOperationException;

    public abstract void delete() throws IOException;

    public String getLocation() {
        return this.location;
    }

    public String getDestination() {
        return this.destination;
    }

    public String getName() {
        return this.name;
    }

    public void setDestination(String destination) {
        this.targetPath = Paths.get(destination);
        this.destination = destination;
    }

    public void move() throws IOException {
        this.copy();
        this.delete();
    }

}
