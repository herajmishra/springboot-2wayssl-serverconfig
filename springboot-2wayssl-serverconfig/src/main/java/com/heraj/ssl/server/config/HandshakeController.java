package com.heraj.ssl.server.config;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandshakeController {
    
    @RequestMapping(path = "handshakeNGC",  method = RequestMethod.GET)
    public String welcome(){
        return "Hello user, Welcome to Heraj's ssl server sample code.";
    }

}
