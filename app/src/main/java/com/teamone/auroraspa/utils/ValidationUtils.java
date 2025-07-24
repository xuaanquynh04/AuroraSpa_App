package com.teamone.auroraspa.utils;

import android.util.Patterns;
import java.util.regex.Pattern;

public class ValidationUtils {
    

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidEmailOrPhone(String input) {
        return isValidEmail(input) || isValidPhone(input);
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.trim().length() >= 6;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && !Character.isDigit(trimmedName.charAt(0));
    }
    

    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
    

    public static String getEmailErrorMessage() {
        return "Email không đúng định dạng";
    }
    

    public static String getPhoneErrorMessage() {
        return "Số điện thoại không đúng định dạng";
    }
    

    public static String getEmailOrPhoneErrorMessage() {
        return "Email hoặc số điện thoại không đúng định dạng";
    }
    

    public static String getPasswordErrorMessage() {
        return "Mật khẩu phải có ít nhất 6 ký tự";
    }
    

    public static String getNameErrorMessage() {
        return "Tên phải có ít nhất 2 ký tự và không được bắt đầu bằng số";
    }
} 