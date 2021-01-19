package org.xm.core.system.worker;

import akka.actor.typed.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.core.system.message.MoveFile;
import org.xm.core.system.message.SystemMessage;

public class ScannerWorker implements Runnable {

    private Boolean canStop;
    private final String connectorPath;
    private final ActorRef<SystemMessage> scanner;
    private static final Logger logger = LoggerFactory.getLogger(ScannerWorker.class);

    public ScannerWorker(String connectorPath, ActorRef<SystemMessage> scanner) {
        this.connectorPath = connectorPath;
        this.scanner = scanner;
        this.canStop = false;
    }

    @Override
    public void run() {
        while (!canStop) {
            scanner.tell(new MoveFile(System.currentTimeMillis(), scanner, scanner, Thread.currentThread().getName() + System.currentTimeMillis()));
        }
    }

    public void canStop() {
        this.canStop = true;
    }


}
