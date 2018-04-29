package com.heraj.ssl.server.config;

import org.springframework.stereotype.Service;

@Service
public class HandshakeService {
    
    public String handshake(String name){
        if (name == null || name.length()<=0){
            return "Hello guest, welcome to Heraj's ssl server sample code.";
        }else{
            return "Hello " + name + ", welcome to Heraj's ssl server sample code.";
        }
    }

}
