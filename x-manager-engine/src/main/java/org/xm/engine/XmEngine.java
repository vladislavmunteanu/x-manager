package org.xm.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.xm.*")
public class XmEngine {

    public static void main(String[] args) {

        SpringApplication.run(XmEngine.class, args);


    }
}
