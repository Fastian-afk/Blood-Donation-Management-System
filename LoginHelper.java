package app.ui;

/**
 * LoginHelper - Singleton-like static class
 * UPDATED: Now stores both userId and username for foreign key constraints.
 */

public class LoginHelper {

    private static String currentUserId;
    private static String currentUsername;
    private static String currentRole;

    private LoginHelper() { }

    public static void login(String userId, String username, String role) {
        currentUserId = userId;
        currentUsername = username;
        currentRole = role;
    }

    public static void logout() {
        currentUserId = null;
        currentUsername = null;
        currentRole = null;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static boolean isLoggedIn() {
        return currentUsername != null && currentRole != null;
    }
}