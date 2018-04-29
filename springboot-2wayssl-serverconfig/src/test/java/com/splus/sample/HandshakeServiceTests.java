package com.splus.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;

import com.heraj.ssl.server.config.HandshakeService;

@RunWith(MockitoJUnitRunner.class)
public class HandshakeServiceTests {
    
    @InjectMocks
    HandshakeService handshakeService = new HandshakeService();

	@Test
	public void hanshakeWithName() {
	    String result = handshakeService.handshake("Heraj");
	    assertEquals(result, "Hello Heraj, welcome to Heraj's ssl server sample code.");
	}
	
	@Test
    public void hanshakeWithoutName() {
        String result = handshakeService.handshake("");
        assertEquals(result, "Hello guest, welcome to Heraj's ssl server sample code.");
    }
	
	@Test
    public void hanshakeWithNull() {
        String result = handshakeService.handshake(null);
        assertEquals(result, "Hello guest, welcome to Heraj's ssl server sample code.");
    }
	

}
