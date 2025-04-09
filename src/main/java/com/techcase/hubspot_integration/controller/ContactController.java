package com.techcase.hubspot_integration.controller;

import com.techcase.hubspot_integration.model.dto.ContactRequest;
import com.techcase.hubspot_integration.service.HubspotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final HubspotService hubspotService;

    public ContactController(HubspotService hubspotService) {
        this.hubspotService = hubspotService;
    }

    @PostMapping
    public ResponseEntity<String> createContact(@RequestBody ContactRequest request) {
        boolean success = hubspotService.createContact(request);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Contato criado com sucesso.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar contato.");
    }
}

