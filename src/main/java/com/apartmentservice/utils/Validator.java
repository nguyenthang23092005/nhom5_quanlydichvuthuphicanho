package com.apartmentservice.utils;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    // ✅ CCCD: phải đúng 12 chữ số
    public static boolean isValidCCCD(String cccd) {
        return cccd != null && cccd.matches("^\\d{12}$");
    }

    // ✅ Số điện thoại: phải bắt đầu bằng 0, có 10 chữ số
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^0\\d{9}$");
    }

    // ✅ Họ tên: không rỗng, không chỉ chứa khoảng trắng, không có ký tự đặc biệt
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[\\p{L} .'-]+$");
    }

    // ✅ Kiểm tra chuỗi là số nguyên dương
    public static boolean isPositiveInteger(String input) {
        try {
            return Integer.parseInt(input.trim()) > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    // ✅ Kiểm tra chuỗi là số thực dương
    public static boolean isPositiveDouble(String input) {
        try {
            return Double.parseDouble(input.trim()) > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    // ✅ Email hợp lệ theo định dạng chuẩn
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    // ✅ Ngày định dạng hợp lệ dd/MM/yyyy
    public static boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(dateStr.trim(), formatter);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }

    // ✅ Chuỗi không chứa ký tự đặc biệt nguy hiểm (chống XSS)
    public static boolean isSafeText(String text) {
        return text != null && text.matches("^[\\p{L}0-9 .,!?\\-()]+$");
    }

    // ✅ Kiểm tra chuỗi không rỗng
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
