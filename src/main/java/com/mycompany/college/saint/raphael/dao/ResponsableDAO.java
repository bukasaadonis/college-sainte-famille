package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Responsable;
import java.sql.*;
import java.util.*;
public class ResponsableDAO {
    public List<Responsable>findAll(String search)throws SQLException {
        List<Responsable>l=new ArrayList<>();
        String s="SELECT r.*,COUNT(er.eleve_id) nombre_eleves FROM responsables r LEFT JOIN eleve_responsable er ON er.responsable_id=r.id WHERE (?='' OR r.nom LIKE ? OR r.prenom LIKE ? OR r.telephone LIKE ?) GROUP BY r.id ORDER BY r.nom,r.prenom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            String q=search==null?"":search.trim(),x="%"+q+"%";
            p.setString(1,q);
            for(int i=2;i<=4;i++)p.setString(i,x);
            try(ResultSet rs=p.executeQuery()) {
                while(rs.next())l.add(map(rs));
            }
        }
        return l;
    }
    public long insert(Responsable o)throws SQLException {
        String s="INSERT INTO responsables(nom,postnom,prenom,lien_avec_eleve,profession,adresse,telephone,email) VALUES(?,?,?,?,?,?,?,?)";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s,Statement.RETURN_GENERATED_KEYS)) {
            bind(p,o);
            p.executeUpdate();
            try(ResultSet r=p.getGeneratedKeys()) {
                r.next();
                return r.getLong(1);
            }
        }
    }
    public void update(Responsable o)throws SQLException {
        String s="UPDATE responsables SET nom=?,postnom=?,prenom=?,lien_avec_eleve=?,profession=?,adresse=?,telephone=?,email=? WHERE id=?";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            bind(p,o);
            p.setLong(9,o.getId());
            p.executeUpdate();
        }
    }
    public void delete(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM responsables WHERE id=?")) {
            p.setLong(1,id);
            p.executeUpdate();
        }
    }
    public void linkStudent(long responsibleId,long studentId)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("INSERT IGNORE INTO eleve_responsable(eleve_id,responsable_id) VALUES(?,?)")) {
            p.setLong(1,studentId);
            p.setLong(2,responsibleId);
            p.executeUpdate();
        }
    }
    private void bind(PreparedStatement p,Responsable o)throws SQLException {
        p.setString(1,o.getNom());
        p.setString(2,o.getPostnom());
        p.setString(3,o.getPrenom());
        p.setString(4,o.getLienAvecEleve());
        p.setString(5,o.getProfession());
        p.setString(6,o.getAdresse());
        p.setString(7,o.getTelephone());
        p.setString(8,o.getEmail());
    }
    private Responsable map(ResultSet r)throws SQLException {
        Responsable o=new Responsable();
        o.setId(r.getLong("id"));
        o.setNom(r.getString("nom"));
        o.setPostnom(r.getString("postnom"));
        o.setPrenom(r.getString("prenom"));
        o.setLienAvecEleve(r.getString("lien_avec_eleve"));
        o.setProfession(r.getString("profession"));
        o.setAdresse(r.getString("adresse"));
        o.setTelephone(r.getString("telephone"));
        o.setEmail(r.getString("email"));
        o.setNombreEleves(r.getInt("nombre_eleves"));
        return o;
    }
}
