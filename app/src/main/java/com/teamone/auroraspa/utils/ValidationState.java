package com.teamone.auroraspa.utils;

import java.util.HashMap;
import java.util.Map;


public class ValidationState {
    private Map<String, Boolean> fieldStates = new HashMap<>();
    private Map<String, String> errorMessages = new HashMap<>();
    

    public void updateFieldState(String fieldName, boolean isValid, String errorMessage) {
        fieldStates.put(fieldName, isValid);
        if (!isValid) {
            errorMessages.put(fieldName, errorMessage);
        } else {
            errorMessages.remove(fieldName);
        }
    }
    

    public boolean isFieldValid(String fieldName) {
        return fieldStates.getOrDefault(fieldName, false);
    }
    

    public String getErrorMessage(String fieldName) {
        return errorMessages.get(fieldName);
    }

    public void reset() {
        fieldStates.clear();
        errorMessages.clear();
    }
} 