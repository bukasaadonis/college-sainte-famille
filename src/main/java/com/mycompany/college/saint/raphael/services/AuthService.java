package com.mycompany.college.saint.raphael.services;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.dao.UtilisateurDAO;
import com.mycompany.college.saint.raphael.models.Utilisateur;
import com.mycompany.college.saint.raphael.security.PasswordHasher;
import com.mycompany.college.saint.raphael.utils.SessionManager;
import java.sql.*;
import java.util.Set;
import java.util.UUID;
public final class AuthService {
    private static final AuthService INSTANCE=new AuthService();
    private final UtilisateurDAO dao=new UtilisateurDAO();
    private AuthService() {
    }
    public static AuthService getInstance() {
        return INSTANCE;
    }
    public Utilisateur login(String login,String password)throws SQLException {
        Utilisateur u=dao.findByLogin(login==null?"":login.trim());
        if(u==null)throw new IllegalArgumentException("Identifiant ou mot de passe incorrect.");
        if(!"ACTIF".equals(u.getStatut()))throw new IllegalArgumentException("Ce compte n'est pas actif.");
        if(u.isBloque())throw new IllegalArgumentException("Ce compte est bloqué. Contactez l'administrateur.");
        if(!PasswordHasher.verify(password,u.getMotDePasse())) {
            dao.registerFailure(u.getId());
            throw new IllegalArgumentException("Identifiant ou mot de passe incorrect.");
        }
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                dao.registerSuccess(c,u.getId());
                long connectionId;
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO connexions(utilisateur_id,adresse_ip,navigateur) VALUES(?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1,u.getId());
                    ps.setString(2,"LOCAL");
                    ps.setString(3,"JavaFX Desktop");
                    ps.executeUpdate();
                    try(ResultSet rs=ps.getGeneratedKeys()) {
                        rs.next();
                        connectionId=rs.getLong(1);
                    }
                }
                String token=UUID.randomUUID().toString();
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO sessions(utilisateur_id,token,adresse_ip,actif) VALUES(?,?,?,1)")) {
                    ps.setLong(1,u.getId());
                    ps.setString(2,token);
                    ps.setString(3,"LOCAL");
                    ps.executeUpdate();
                }
                c.commit();
                Set<String> permissions=dao.findPermissions(u.getId());
                u=dao.findByLogin(u.getLogin());
                SessionManager.getInstance().open(u,permissions,connectionId,token);
                AuditService.getInstance().log("CONNEXION","UTILISATEUR",u.getId(),"Ouverture de session");
                return u;
            }
            catch(Exception e) {
                c.rollback();
                if(e instanceof SQLException s)throw s;
                throw new SQLException(e);
            }
        }
    }
    public void logout() {
        SessionManager s=SessionManager.getInstance();
        Long uid=s.getCurrentUserId();
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            if(s.getConnectionId()!=null)try(PreparedStatement ps=c.prepareStatement("UPDATE connexions SET date_deconnexion=NOW() WHERE id=?")) {
                ps.setLong(1,s.getConnectionId());
                ps.executeUpdate();
            }
            if(s.getSessionToken()!=null)try(PreparedStatement ps=c.prepareStatement("UPDATE sessions SET actif=0,date_fin=NOW() WHERE token=?")) {
                ps.setString(1,s.getSessionToken());
                ps.executeUpdate();
            }
        }
        catch(SQLException ignored) {
        }
        if(uid!=null)AuditService.getInstance().log("DECONNEXION","UTILISATEUR",uid,"Fermeture de session");
        s.clear();
    }
}
