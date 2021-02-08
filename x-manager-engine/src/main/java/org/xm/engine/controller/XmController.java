package org.xm.engine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xm.core.Configuration;
import org.xm.engine.services.XmService;

@RestController
@RequestMapping("/api")
public class XmController {

    private XmService xmService;

    @Autowired
    public XmController(XmService xmService){
        this.xmService = xmService;
    }

    @GetMapping(value = "/conf")
    public ResponseEntity<Configuration> getConf() {
        return new ResponseEntity<>(xmService.getManagerConfiguration(), HttpStatus.OK);
    }

}
