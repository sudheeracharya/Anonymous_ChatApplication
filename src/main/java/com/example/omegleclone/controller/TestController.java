// File: src/main/java/com/example/omegleclone/controller/TestController.java

package com.example.omegleclone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/websocket-status")
    public String getWebSocketStatus() {
        return "WebSocket service is running";
    }
}

