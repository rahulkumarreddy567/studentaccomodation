package com.pandalodge.util;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class IconUtil {
    public static Node createIcon(String iconLiteral, String fallbackUnicode) {
        try {
            Class<?> fontIconClass = Class.forName("org.kordamp.ikonli.javafx.FontIcon");
            Object fontIcon = fontIconClass.getDeclaredConstructor().newInstance();
            fontIconClass.getMethod("setIconLiteral", String.class).invoke(fontIcon, iconLiteral);
            return (Node) fontIcon;
        } catch (Throwable t) {
            Label l = new Label(fallbackUnicode != null ? fallbackUnicode : "");
            return l;
        }
    }
}

