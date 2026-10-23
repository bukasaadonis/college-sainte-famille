package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Role;
import com.mycompany.college.saint.raphael.models.Utilisateur;
import java.sql.*;
import java.util.*;
public class UtilisateurDAO {
    private static final String BASE = "SELECT u.*, r.nom role_nom FROM utilisateurs u JOIN roles r ON r.id=u.role_id ";
    public Utilisateur findByLogin(String login) throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement(BASE+"WHERE u.login=? LIMIT 1")) {
            ps.setString(1,login);
            try(ResultSet rs=ps.executeQuery()) {
                return rs.next()?map(rs):null;
            }
        }
    }
    public List<Utilisateur> findAll(String search) throws SQLException {
        List<Utilisateur> list=new ArrayList<>();
        String sql=BASE+"WHERE (?='' OR u.login LIKE ? OR u.nom LIKE ? OR u.prenom LIKE ? OR u.email LIKE ?) ORDER BY u.nom,u.prenom";
        try(Connection c=DatabaseConfig.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            String q=search==null?"":search.trim(), like="%"+q+"%";
            ps.setString(1,q);
            for(int i=2;i<=5;i++)ps.setString(i,like);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next())list.add(map(rs));
            }
        }
        return list;
    }
    public List<Role> findRoles() throws SQLException {
        List<Role> list=new ArrayList<>();
        try(Connection c=DatabaseConfig.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement("SELECT * FROM roles ORDER BY nom"); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) {
                Role r=new Role();
                r.setId(rs.getLong("id"));
                r.setNom(rs.getString("nom"));
                r.setDescription(rs.getString("description"));
                list.add(r);
            }
        }
        return list;
    }
    public Set<String> findPermissions(long userId) throws SQLException {
        Set<String> set=new HashSet<>();
        String sql="SELECT p.code FROM utilisateurs u JOIN role_permissions rp ON rp.role_id=u.role_id JOIN permissions p ON p.id=rp.permission_id WHERE u.id=?";
        try(Connection c=DatabaseConfig.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next())set.add(rs.getString(1));
            }
        }
        return set;
    }
    public long insert(Utilisateur u) throws SQLException {
        String sql="INSERT INTO utilisateurs(role_id,login,mot_de_passe,nom,postnom,prenom,email,telephone,statut) VALUES(?,?,?,?,?,?,?,?,?)";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            bind(ps,u,true);
            ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()) {
                if(rs.next())return rs.getLong(1);
            }
            throw new SQLException("ID utilisateur non généré");
        }
    }
    public void update(Utilisateur u, boolean changePassword) throws SQLException {
        String sql=changePassword?"UPDATE utilisateurs SET role_id=?,login=?,mot_de_passe=?,nom=?,postnom=?,prenom=?,email=?,telephone=?,statut=?,date_modification=NOW() WHERE id=?":"UPDATE utilisateurs SET role_id=?,login=?,nom=?,postnom=?,prenom=?,email=?,telephone=?,statut=?,date_modification=NOW() WHERE id=?";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            int i=1;
            ps.setLong(i++,u.getRoleId());
            ps.setString(i++,u.getLogin());
            if(changePassword)ps.setString(i++,u.getMotDePasse());
            ps.setString(i++,u.getNom());
            ps.setString(i++,u.getPostnom());
            ps.setString(i++,u.getPrenom());
            ps.setString(i++,u.getEmail());
            ps.setString(i++,u.getTelephone());
            ps.setString(i++,u.getStatut());
            ps.setLong(i,u.getId());
            ps.executeUpdate();
        }
    }
    public void setBlocked(long id, boolean blocked) throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement("UPDATE utilisateurs SET bloque=?,tentative_echec=0,date_modification=NOW() WHERE id=?")) {
            ps.setBoolean(1,blocked);
            ps.setLong(2,id);
            ps.executeUpdate();
        }
    }
    public void registerSuccess(Connection c,long id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE utilisateurs SET tentative_echec=0,bloque=0,derniere_connexion=NOW() WHERE id=?")) {
            ps.setLong(1,id);
            ps.executeUpdate();
        }
    }
    public void registerFailure(long id) throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement("UPDATE utilisateurs SET tentative_echec=tentative_echec+1,bloque=IF(tentative_echec+1>=3,1,bloque) WHERE id=?")) {
            ps.setLong(1,id);
            ps.executeUpdate();
        }
    }
    private void bind(PreparedStatement ps,Utilisateur u,boolean pwd)throws SQLException {
        int i=1;
        ps.setLong(i++,u.getRoleId());
        ps.setString(i++,u.getLogin());
        if(pwd)ps.setString(i++,u.getMotDePasse());
        ps.setString(i++,u.getNom());
        ps.setString(i++,u.getPostnom());
        ps.setString(i++,u.getPrenom());
        ps.setString(i++,u.getEmail());
        ps.setString(i++,u.getTelephone());
        ps.setString(i,u.getStatut());
    }
    private Utilisateur map(ResultSet rs)throws SQLException {
        Utilisateur u=new Utilisateur();
        u.setId(rs.getLong("id"));
        u.setRoleId(rs.getLong("role_id"));
        u.setRoleNom(rs.getString("role_nom"));
        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setNom(rs.getString("nom"));
        u.setPostnom(rs.getString("postnom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setTelephone(rs.getString("telephone"));
        u.setStatut(rs.getString("statut"));
        Timestamp d=rs.getTimestamp("date_creation");
        if(d!=null)u.setDateCreation(d.toLocalDateTime());
        Timestamp l=rs.getTimestamp("derniere_connexion");
        if(l!=null)u.setDerniereConnexion(l.toLocalDateTime());
        u.setTentativeEchec(rs.getInt("tentative_echec"));
        u.setBloque(rs.getBoolean("bloque"));
        return u;
    }
}
