package org.xm.core.system.worker;

import akka.actor.typed.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.core.system.file.XmFile;
import org.xm.core.system.file.XmItem;
import org.xm.core.system.message.RouteFile;
import org.xm.core.system.message.SystemMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ScannerWorker implements Runnable {

    private Boolean canStop;
    private final String connectorPath;
    private final ActorRef<SystemMessage> scanner;
    private static final Logger logger = LoggerFactory.getLogger(ScannerWorker.class);
    private Set<String> filesCache;

    public ScannerWorker(String connectorPath, ActorRef<SystemMessage> scanner) {
        this.connectorPath = connectorPath;
        this.scanner = scanner;
        this.canStop = false;
        this.filesCache = new HashSet<>();
    }

    @Override
    public void run() {
        while (!canStop) {
            String fileToProcess = scan(connectorPath);
            if (fileToProcess != null) {
                Long requestId = fileToProcess.hashCode() + System.currentTimeMillis();
                XmItem fileToRoute = new XmFile(fileToProcess);
                SystemMessage routeFileMessage = new RouteFile(requestId, scanner, scanner, fileToRoute);
                scanner.tell(routeFileMessage);
            } else {
                if (filesCache.size() > 0)
                    filesCache.clear();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace(); //TODO send a message to restart
                }
            }
        }
    }

    private String scan(String connectorPath) {
        File connector = new File(connectorPath);
        File[] files = connector.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.getName().startsWith("."))
                    filesCache.add(file.getAbsolutePath());
                if (file.isFile() && !filesCache.contains(file.getAbsolutePath()) && !file.getName().endsWith(".lock")) {
                    Path lockedFilePath = Paths.get(String.format("%s.lock", file));
                    try {
                        Files.createFile(lockedFilePath);
                        filesCache.add(file.getAbsolutePath());
                        return file.getAbsolutePath();
                    } catch (IOException e) {
                        //logger.warn("Skip file '{}' as it's already in progress", file, e);
                    }

                } else if (file.isDirectory() && !filesCache.contains(file.getAbsolutePath())) {
                    return scan(file.getAbsolutePath());
                }
            }
        } else if (!connectorPath.equals(this.connectorPath) && !connectorPath.endsWith(".lock")) {
            Path lockedFolderPath = Paths.get(String.format("%s.lock", connectorPath));
            try {
                Files.createFile(lockedFolderPath);
                filesCache.add(connectorPath);
            } catch (IOException e) {
                logger.warn("Folder '{}' was deleted already", connectorPath);
            }
            try {
                Files.delete(Paths.get(connectorPath));
                Files.delete(lockedFolderPath);
                filesCache.remove(connectorPath);
            } catch (IOException e) {
                logger.error("Failed to delete empty folder '{}'", connectorPath, e);
            }
        }

        return null;
    }

    public void canStop() {
        this.canStop = true;
    }


}
