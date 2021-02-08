package org.xm.engine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xm.core.Configuration;
import org.xm.core.XManager;

@Service
public class XmService {


    private XManager xManager;

    @Autowired
    public XmService(XManager xManager) {
        this.xManager = xManager;
    }

    public Configuration getManagerConfiguration(){
        return xManager.getConf();
    }
}
