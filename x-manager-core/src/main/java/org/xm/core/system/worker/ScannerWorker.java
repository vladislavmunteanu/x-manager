package org.xm.core.system.worker;

import akka.actor.typed.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.core.system.message.MoveFile;
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
            String fileToProcess = scan();
            if (fileToProcess != null) {
                Long requestId = fileToProcess.hashCode() + System.currentTimeMillis();
                SystemMessage moveFile = new MoveFile(requestId, scanner, scanner, fileToProcess);
                scanner.tell(moveFile);
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

    private String scan() {
        File connector = new File(connectorPath);
        File[] files = connector.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!filesCache.contains(file.getAbsolutePath()) && file.isFile() && !file.getName().endsWith(".lock")) {
                    Path lockFilePath = Paths.get(String.format("%s.lock", file));
                    try {
                        Files.createFile(lockFilePath);
                        filesCache.add(file.getAbsolutePath());
                        return file.getAbsolutePath();
                    } catch (IOException e) {
                        logger.debug("Skip file '{}' as it's already in progress", file);
                    }
                }
            }
        }
        return null;
    }

    public void canStop() {
        this.canStop = true;
    }


}
