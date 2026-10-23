package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Enseignant;
import java.sql.*;
import java.util.*;
public class EnseignantDAO {
    public List<Enseignant>findAll(String search)throws SQLException {
        List<Enseignant>l=new ArrayList<>();
        String s="SELECT e.*,COUNT(c.id) nombre_cours FROM enseignants e LEFT JOIN cours c ON c.enseignant_id=e.id WHERE (?='' OR e.nom LIKE ? OR e.prenom LIKE ? OR e.specialite LIKE ?) GROUP BY e.id ORDER BY e.nom,e.prenom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            String q=search==null?"":search.trim(),x="%"+q+"%";
            p.setString(1,q);
            for(int i=2;i<=4;i++)p.setString(i,x);
            try(ResultSet r=p.executeQuery()) {
                while(r.next())l.add(map(r));
            }
        }
        return l;
    }
    public long insert(Enseignant o)throws SQLException {
        String s="INSERT INTO enseignants(nom,postnom,prenom,specialite,email,telephone) VALUES(?,?,?,?,?,?)";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s,Statement.RETURN_GENERATED_KEYS)) {
            bind(p,o);
            p.executeUpdate();
            try(ResultSet r=p.getGeneratedKeys()) {
                r.next();
                return r.getLong(1);
            }
        }
    }
    public void update(Enseignant o)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("UPDATE enseignants SET nom=?,postnom=?,prenom=?,specialite=?,email=?,telephone=? WHERE id=?")) {
            bind(p,o);
            p.setLong(7,o.getId());
            p.executeUpdate();
        }
    }
    public void delete(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM enseignants WHERE id=?")) {
            p.setLong(1,id);
            p.executeUpdate();
        }
    }
    private void bind(PreparedStatement p,Enseignant o)throws SQLException {
        p.setString(1,o.getNom());
        p.setString(2,o.getPostnom());
        p.setString(3,o.getPrenom());
        p.setString(4,o.getSpecialite());
        p.setString(5,o.getEmail());
        p.setString(6,o.getTelephone());
    }
    private Enseignant map(ResultSet r)throws SQLException {
        Enseignant o=new Enseignant();
        o.setId(r.getLong("id"));
        o.setNom(r.getString("nom"));
        o.setPostnom(r.getString("postnom"));
        o.setPrenom(r.getString("prenom"));
        o.setSpecialite(r.getString("specialite"));
        o.setEmail(r.getString("email"));
        o.setTelephone(r.getString("telephone"));
        o.setNombreCours(r.getInt("nombre_cours"));
        return o;
    }
}
