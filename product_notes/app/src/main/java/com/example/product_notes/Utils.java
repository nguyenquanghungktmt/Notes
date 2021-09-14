package com.example.product_notes;

public class Utils {

    public static String colorIntToHexString(int color) {
        return String.format("#%08X", (0xFFFFFFFF & color));
    }
}
