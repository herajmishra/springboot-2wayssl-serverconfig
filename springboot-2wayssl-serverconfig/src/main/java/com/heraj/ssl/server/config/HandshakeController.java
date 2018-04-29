package com.heraj.ssl.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandshakeController {
    
    @Autowired
    HandshakeService handshakeService;
    
    @RequestMapping(path = "handshakeNGC",  method = RequestMethod.GET)
    public String welcome(@RequestParam(value = "name", required = false) String name){
        return handshakeService.handshake(name);
    }

}
