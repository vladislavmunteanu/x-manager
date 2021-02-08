package org.xm.core.system.file;

import org.xm.core.exception.UnsupportedOperationException;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class XmFile extends XmItem {

    private FileChannel itemChannel;
    private FileLock itemLock;


    public XmFile(String location) {
        super(location);
    }

    @Override
    public Boolean isFile() {
        return true;
    }

    @Override
    public void copy() throws IOException {
        if (this.destination != null)
            Files.copy(this.sourcePath, this.targetPath, StandardCopyOption.REPLACE_EXISTING);
        else
            throw new UnsupportedOperationException(String.format("Unknown destination for '%s'", this.getLocation()));
    }

    @Override
    public void delete() throws IOException {
        Files.delete(this.sourcePath);
        if (this.getItemLock() != null)
            this.getItemLock().release();
        if (this.getItemChannel() != null)
            this.getItemChannel().close();
    }

    public FileChannel getItemChannel() {
        return itemChannel;
    }

    public void setItemChannel(FileChannel itemChannel) {
        this.itemChannel = itemChannel;
    }

    public FileLock getItemLock() {
        return itemLock;
    }

    public void setItemLock(FileLock itemLock) {
        this.itemLock = itemLock;
    }
}
