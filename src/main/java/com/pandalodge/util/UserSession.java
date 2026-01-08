package com.pandalodge.util;

import com.pandalodge.model.Student;

public class UserSession {
    private static Student currentStudent;
    private static boolean isAdmin;

    public static void login(Student student) {
        currentStudent = student;
        isAdmin = false;
    }

    public static void loginAdmin() {
        currentStudent = null;
        isAdmin = true;
    }

    public static void logout() {
        currentStudent = null;
        isAdmin = false;
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static boolean isLoggedIn() {
        return currentStudent != null || isAdmin;
    }
}










