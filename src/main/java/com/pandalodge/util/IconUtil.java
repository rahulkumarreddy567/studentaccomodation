package com.pandalodge.util;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Helper to create an icon Node using Ikonli FontIcon when available, otherwise fall back to a Unicode label.
 */
public class IconUtil {
    public static Node createIcon(String iconLiteral, String fallbackUnicode) {
        try {
            // Try to create FontIcon via reflection to avoid hard dependency at runtime
            Class<?> fontIconClass = Class.forName("org.kordamp.ikonli.javafx.FontIcon");
            Object fontIcon = fontIconClass.getDeclaredConstructor().newInstance();
            // call setIconLiteral
            fontIconClass.getMethod("setIconLiteral", String.class).invoke(fontIcon, iconLiteral);
            return (Node) fontIcon;
        } catch (Throwable t) {
            // fallback to unicode emoji/label
            Label l = new Label(fallbackUnicode != null ? fallbackUnicode : "");
            return l;
        }
    }
}











