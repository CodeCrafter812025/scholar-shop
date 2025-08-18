package net.holosen.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    /**
     * هش کردن یک رشته با استفاده از الگوریتم SHA-1
     *
     * @param input رشته‌ای که می‌خواهید هش شود
     * @return رشته هگزادسیمال هش شده
     */
    public static String sha1Hash(String input) {
        try {
            // ایجاد یک شیء MessageDigest با الگوریتم SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // تبدیل رشته ورودی به آرایه بایت و محاسبه هش
            byte[] messageDigest = md.digest(input.getBytes());

            // تبدیل آرایه بایت به رشته هگزادسیمال
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // در صورتی که الگوریتم SHA-1 در دسترس نباشد، یک استثنا پرتاب می‌شود
            throw new RuntimeException(e);
        }
    }

}