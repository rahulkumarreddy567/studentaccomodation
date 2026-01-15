package com.pandalodge.util;

import com.pandalodge.model.Student;
import com.pandalodge.model.Renter;

public class UserSession {
    private static Student currentStudent;
    private static Renter currentRenter;
    private static boolean isAdmin;

    public static void login(Student student) {
        currentStudent = student;
        currentRenter = null;
        isAdmin = false;
    }

    public static void loginRenter(Renter renter) {
        currentRenter = renter;
        currentStudent = null;
        isAdmin = false;
    }

    public static void loginAdmin() {
        currentStudent = null;
        currentRenter = null;
        isAdmin = true;
    }

    public static void logout() {
        currentStudent = null;
        currentRenter = null;
        isAdmin = false;
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static Renter getCurrentRenter() {
        return currentRenter;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static boolean isRenter() {
        return currentRenter != null;
    }

    public static boolean isLoggedIn() {
        return currentStudent != null || currentRenter != null || isAdmin;
    }
}
