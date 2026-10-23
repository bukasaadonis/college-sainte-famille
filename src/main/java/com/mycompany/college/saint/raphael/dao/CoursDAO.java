package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Cours;
import java.sql.*;
import java.util.*;
public class CoursDAO {
    public List<Cours>findAll(String search)throws SQLException {
        List<Cours>l=new ArrayList<>();
        String s="SELECT c.*,cl.nom classe_nom,CONCAT(COALESCE(e.nom,''),' ',COALESCE(e.prenom,'')) enseignant_nom FROM cours c JOIN classes cl ON cl.id=c.classe_id LEFT JOIN enseignants e ON e.id=c.enseignant_id WHERE (?='' OR c.nom LIKE ? OR c.code LIKE ? OR cl.nom LIKE ?) ORDER BY cl.nom,c.nom";
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
    public List<Cours>findByClass(long classId)throws SQLException {
        List<Cours>l=new ArrayList<>();
        String s="SELECT c.*,cl.nom classe_nom,CONCAT(COALESCE(e.nom,''),' ',COALESCE(e.prenom,'')) enseignant_nom FROM cours c JOIN classes cl ON cl.id=c.classe_id LEFT JOIN enseignants e ON e.id=c.enseignant_id WHERE c.classe_id=? ORDER BY c.nom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            p.setLong(1,classId);
            try(ResultSet r=p.executeQuery()) {
                while(r.next())l.add(map(r));
            }
        }
        return l;
    }
    public long insert(Cours o)throws SQLException {
        String s="INSERT INTO cours(enseignant_id,classe_id,nom,code,coefficient,description) VALUES(?,?,?,?,?,?)";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s,Statement.RETURN_GENERATED_KEYS)) {
            bind(p,o);
            p.executeUpdate();
            try(ResultSet r=p.getGeneratedKeys()) {
                r.next();
                return r.getLong(1);
            }
        }
    }
    public void update(Cours o)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("UPDATE cours SET enseignant_id=?,classe_id=?,nom=?,code=?,coefficient=?,description=? WHERE id=?")) {
            bind(p,o);
            p.setLong(7,o.getId());
            p.executeUpdate();
        }
    }
    public void delete(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            try(PreparedStatement check=c.prepareStatement("SELECT COUNT(*) FROM notes WHERE cours_id=?")) {
                check.setLong(1,id);
                try(ResultSet rs=check.executeQuery()) {
                    rs.next();
                    if(rs.getInt(1)>0)throw new SQLException("Ce cours contient déjà des notes. Sa suppression est bloquée afin de préserver les résultats scolaires.");
                }
            }
            try(PreparedStatement p=c.prepareStatement("DELETE FROM cours WHERE id=?")) {
                p.setLong(1,id);
                p.executeUpdate();
            }
        }
    }
    private void bind(PreparedStatement p,Cours o)throws SQLException {
        if(o.getEnseignantId()==null)p.setNull(1,Types.BIGINT);
        else p.setLong(1,o.getEnseignantId());
        p.setLong(2,o.getClasseId());
        p.setString(3,o.getNom());
        p.setString(4,o.getCode());
        p.setDouble(5,o.getCoefficient());
        p.setString(6,o.getDescription());
    }
    private Cours map(ResultSet r)throws SQLException {
        Cours o=new Cours();
        o.setId(r.getLong("id"));
        long eid=r.getLong("enseignant_id");
        o.setEnseignantId(r.wasNull()?null:eid);
        o.setClasseId(r.getLong("classe_id"));
        o.setNom(r.getString("nom"));
        o.setCode(r.getString("code"));
        o.setCoefficient(r.getDouble("coefficient"));
        o.setDescription(r.getString("description"));
        o.setClasseNom(r.getString("classe_nom"));
        o.setEnseignantNom(r.getString("enseignant_nom").trim());
        return o;
    }
}
