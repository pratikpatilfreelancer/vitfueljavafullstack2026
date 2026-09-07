package util;

import model.User;

/**
 * Holds the currently logged-in user for the lifetime of the desktop
 * application (a single JVM instance = a single session).
 */
public class SessionManager {

    private static User currentUser;
    private static Integer currentCandidateId;
    private static Integer currentRecruiterId;

    private SessionManager() {}

    public static void login(User user) {
        currentUser = user;
    }

    public static void setCandidateId(int candidateId) {
        currentCandidateId = candidateId;
    }

    public static void setRecruiterId(int recruiterId) {
        currentRecruiterId = recruiterId;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Integer getCurrentCandidateId() {
        return currentCandidateId;
    }

    public static Integer getCurrentRecruiterId() {
        return currentRecruiterId;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
        currentCandidateId = null;
        currentRecruiterId = null;
    }
}
