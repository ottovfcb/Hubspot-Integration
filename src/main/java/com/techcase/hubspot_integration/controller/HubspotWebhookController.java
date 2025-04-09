package com.techcase.hubspot_integration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class HubspotWebhookController {

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody List<Map<String, Object>> events) {
        System.out.println("Eventos recebidos do HubSpot:");
        events.forEach(System.out::println);
        return ResponseEntity.ok().build();
    }
}