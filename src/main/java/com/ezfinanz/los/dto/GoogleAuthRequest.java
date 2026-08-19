package com.ezfinanz.los.dto;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String email;
    private String fullName;
    private String googleId; // The 'sub' field from Google JWT
}
