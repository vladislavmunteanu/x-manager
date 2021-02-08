package org.xm.core.system.command;

import org.xm.core.system.file.XmItem;
import java.io.IOException;


public class Commands {

    protected static String MOVE_FILE = "MoveFile";
    protected static String DELETE_FILE = "DeleteFile";
    protected static String COPY_FILE = "CopyFile";
    protected static String ROUTE_FILE = "RouteFile";

    public static class MoveFile extends Command {

        public MoveFile(XmItem xmItem) {
            super(MOVE_FILE, xmItem);
        }

        @Override
        public void execute() throws IOException {
            xmItem.move();
        }
    }

    public static class DeleteFile extends Command {

        public DeleteFile(XmItem xmItem) {
            super(DELETE_FILE, xmItem);
        }

        @Override
        public void execute() throws IOException {
            xmItem.delete();
        }
    }


    public static class CopyFile extends Command {

        public CopyFile(XmItem xmItem) {
            super(COPY_FILE, xmItem);
        }

        @Override
        public void execute() throws IOException {
            xmItem.copy();
        }
    }

    public static class RouteFile extends Command {

        public RouteFile(XmItem xmItem) {
            super(ROUTE_FILE, xmItem);
        }

        @Override
        public void execute() throws IOException {
            xmItem.move();
         }
    }
}
