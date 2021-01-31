package org.xm.core.system.file;

import org.xm.core.exception.UnsupportedOperationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class XmFile extends XmItem {

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
    }
}
