package com.techcase.hubspot_integration.model.dto;

import lombok.Data;

@Data
public class ContactRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
