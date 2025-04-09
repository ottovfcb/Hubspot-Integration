package com.techcase.hubspot_integration.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContactRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
