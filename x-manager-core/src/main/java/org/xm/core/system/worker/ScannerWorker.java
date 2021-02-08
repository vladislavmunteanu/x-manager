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
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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
            XmItem fileToProcess = null;
            try {
                fileToProcess = scan(connectorPath);
            } catch (IOException e) {
                logger.error("Failed to scan connector directory.", e);
            }
            if (fileToProcess != null) {
                Long requestId = fileToProcess.getLocation().hashCode() + System.currentTimeMillis();
                SystemMessage routeFileMessage = new RouteFile(requestId, scanner, scanner, fileToProcess);
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

    public void canStop() {
        this.canStop = true;
    }

    public void resume() {
        this.canStop = false;
    }

    private XmItem scan(String connectorPath) throws IOException {
        File connector = new File(connectorPath);
        File[] files = connector.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                String fileName = file.getName();
                String filePath = file.getAbsolutePath();
                if (fileName.startsWith("."))
                    filesCache.add(filePath);
                if (!filesCache.contains(filePath)) {
                    if (file.isFile()) {
                        FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
                        FileLock lock;
                        try {
                            lock = channel.lock();
                            XmItem xmFile = new XmFile(filePath);
                            ((XmFile) xmFile).setItemChannel(channel);
                            ((XmFile) xmFile).setItemLock(lock);
                            filesCache.add(filePath);
                            return xmFile;
                        } catch (OverlappingFileLockException ignored) {
                            //logger.info("Skipping file: " + filePath);
                        }
                    }

                    if (file.isDirectory()) {
                        if (isEmpty(filePath))
                            Files.delete(Paths.get(filePath));
                        else {
                            XmItem xmFile = scan(filePath);
                            if (xmFile != null)
                                return xmFile;
                        }
                    }
                }
            }
        }
        return null;
    }


    private boolean isEmpty(String path) throws IOException {
        Path dirPath = Paths.get(path);
        if (Files.isDirectory(dirPath)) {
            try (Stream<Path> entries = Files.list(dirPath)) {
                return !entries.findFirst().isPresent();
            }
        }
        return false;
    }


}
