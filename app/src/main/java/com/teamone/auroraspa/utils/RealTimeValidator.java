package com.teamone.auroraspa.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class RealTimeValidator {
    
    public interface ValidationCallback {
        void onValidationResult(boolean isValid, String errorMessage);
    }

    public static void addEmailValidation(EditText editText, TextView errorTextView, ValidationCallback callback) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                boolean isValid = true;
                String errorMessage = "";
                
                if (ValidationUtils.isEmpty(email)) {
                    isValid = false;
                    errorMessage = "Vui lòng nhập email";
                } else if (!ValidationUtils.isValidEmail(email)) {
                    isValid = false;
                    errorMessage = ValidationUtils.getEmailErrorMessage();
                }
                
                showError(errorTextView, errorMessage);
                if (callback != null) {
                    callback.onValidationResult(isValid, errorMessage);
                }
            }
        });
    }

    public static void addPhoneValidation(EditText editText, TextView errorTextView, ValidationCallback callback) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();
                boolean isValid = true;
                String errorMessage = "";
                
                if (ValidationUtils.isEmpty(phone)) {
                    isValid = false;
                    errorMessage = "Vui lòng nhập số điện thoại";
                } else if (!ValidationUtils.isValidPhone(phone)) {
                    isValid = false;
                    errorMessage = ValidationUtils.getPhoneErrorMessage();
                }
                
                showError(errorTextView, errorMessage);
                if (callback != null) {
                    callback.onValidationResult(isValid, errorMessage);
                }
            }
        });
    }

    public static void addNameValidation(EditText editText, TextView errorTextView, ValidationCallback callback) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();
                boolean isValid = true;
                String errorMessage = "";
                
                if (ValidationUtils.isEmpty(name)) {
                    isValid = false;
                    errorMessage = "Vui lòng nhập họ và tên";
                } else if (!ValidationUtils.isValidName(name)) {
                    isValid = false;
                    errorMessage = ValidationUtils.getNameErrorMessage();
                }
                
                showError(errorTextView, errorMessage);
                if (callback != null) {
                    callback.onValidationResult(isValid, errorMessage);
                }
            }
        });
    }

    public static void addPasswordValidation(EditText editText, TextView errorTextView, ValidationCallback callback) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                boolean isValid = true;
                String errorMessage = "";
                
                if (ValidationUtils.isEmpty(password)) {
                    isValid = false;
                    errorMessage = "Vui lòng nhập mật khẩu";
                } else if (!ValidationUtils.isValidPassword(password)) {
                    isValid = false;
                    errorMessage = ValidationUtils.getPasswordErrorMessage();
                }
                
                showError(errorTextView, errorMessage);
                if (callback != null) {
                    callback.onValidationResult(isValid, errorMessage);
                }
            }
        });
    }

    public static void addEmailOrPhoneValidation(EditText editText, TextView errorTextView, ValidationCallback callback) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                boolean isValid = true;
                String errorMessage = "";
                
                if (ValidationUtils.isEmpty(input)) {
                    isValid = false;
                    errorMessage = "Vui lòng nhập email hoặc số điện thoại";
                } else if (!ValidationUtils.isValidEmailOrPhone(input)) {
                    isValid = false;
                    errorMessage = ValidationUtils.getEmailOrPhoneErrorMessage();
                }
                
                showError(errorTextView, errorMessage);
                if (callback != null) {
                    callback.onValidationResult(isValid, errorMessage);
                }
            }
        });
    }

    public static void addConfirmPasswordValidation(EditText passwordEditText, EditText confirmPasswordEditText, TextView errorTextView, ValidationCallback callback) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                boolean isValid = true;
                String errorMessage = "";
                
                if (ValidationUtils.isEmpty(confirmPassword)) {
                    isValid = false;
                    errorMessage = "Vui lòng nhập lại mật khẩu";
                } else if (!ValidationUtils.isValidPassword(confirmPassword)) {
                    isValid = false;
                    errorMessage = ValidationUtils.getPasswordErrorMessage();
                } else if (!password.equals(confirmPassword)) {
                    isValid = false;
                    errorMessage = "Mật khẩu nhập lại không khớp";
                }
                
                showError(errorTextView, errorMessage);
                if (callback != null) {
                    callback.onValidationResult(isValid, errorMessage);
                }
            }
        };

        passwordEditText.addTextChangedListener(textWatcher);
        confirmPasswordEditText.addTextChangedListener(textWatcher);
    }

    private static void showError(TextView errorTextView, String errorMessage) {
        if (errorTextView != null) {
            if (errorMessage.isEmpty()) {
                errorTextView.setVisibility(android.view.View.GONE);
                errorTextView.setText("");
            } else {
                errorTextView.setVisibility(android.view.View.VISIBLE);
                errorTextView.setText(errorMessage);
            }
        }
    }
} 