package com.mycompany.college.saint.raphael.utils;
import com.mycompany.college.saint.raphael.models.Utilisateur;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
public final class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private Utilisateur currentUser;
    private Set<String> permissions = new HashSet<>();
    private Long connectionId;
    private String sessionToken;
    private SessionManager() {
    }
    public static SessionManager getInstance() {
        return INSTANCE;
    }
    public void open(Utilisateur user, Set<String> userPermissions, Long connectionId, String token) {
        this.currentUser = user;
        this.permissions = new HashSet<>(userPermissions == null ? Collections.emptySet() : userPermissions);
        this.connectionId = connectionId;
        this.sessionToken = token;
    }
    public void clear() {
        currentUser = null;
        permissions.clear();
        connectionId = null;
        sessionToken = null;
    }
    public Utilisateur getCurrentUser() {
        return currentUser;
    }
    public Long getCurrentUserId() {
        return currentUser == null ? null : currentUser.getId();
    }
    public boolean hasPermission(String code) {
        return currentUser != null && ("ADMIN".equals(currentUser.getRoleNom()) || permissions.contains(code));
    }
    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
    public Long getConnectionId() {
        return connectionId;
    }
    public String getSessionToken() {
        return sessionToken;
    }
}
