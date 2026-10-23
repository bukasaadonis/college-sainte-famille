package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Classe;
import java.sql.*;
import java.util.*;
public class ClasseDAO {
    public List<Classe> findAll(String search)throws SQLException {
        List<Classe>l=new ArrayList<>();
        String sql="SELECT c.*,COUNT(i.id) effectif FROM classes c LEFT JOIN inscriptions i ON i.classe_id=c.id AND i.statut='ACTIVE' AND i.annee_scolaire_id=(SELECT id FROM annees_scolaires WHERE est_active=1 ORDER BY date_debut DESC LIMIT 1) WHERE (?='' OR c.nom LIKE ? OR c.niveau LIKE ? OR c.section LIKE ?) GROUP BY c.id ORDER BY c.nom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql)) {
            String q=search==null?"":search.trim(),x="%"+q+"%";
            p.setString(1,q);
            for(int i=2;i<=4;i++)p.setString(i,x);
            try(ResultSet r=p.executeQuery()) {
                while(r.next())l.add(map(r));
            }
        }
        return l;
    }
    public List<Classe> findActive()throws SQLException {
        return findAll("").stream().filter(c->"ACTIVE".equals(c.getStatut())).toList();
    }
    public long insert(Classe o)throws SQLException {
        String s="INSERT INTO classes(nom,niveau,section,option,capacite_max,statut) VALUES(?,?,?,?,?,?)";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s,Statement.RETURN_GENERATED_KEYS)) {
            bind(p,o);
            p.executeUpdate();
            try(ResultSet r=p.getGeneratedKeys()) {
                r.next();
                return r.getLong(1);
            }
        }
    }
    public void update(Classe o)throws SQLException {
        String s="UPDATE classes SET nom=?,niveau=?,section=?,option=?,capacite_max=?,statut=? WHERE id=?";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            bind(p,o);
            p.setLong(7,o.getId());
            p.executeUpdate();
        }
    }
    public void delete(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement chk=c.prepareStatement("SELECT COUNT(*) FROM inscriptions WHERE classe_id=?")) {
            chk.setLong(1,id);
            try(ResultSet r=chk.executeQuery()) {
                r.next();
                if(r.getInt(1)>0)throw new SQLException("Cette classe possède un historique d’inscriptions. Placez plutôt son statut sur INACTIVE pour préserver les données scolaires.");
            }
            try(PreparedStatement p=c.prepareStatement("DELETE FROM classes WHERE id=?")) {
                p.setLong(1,id);
                p.executeUpdate();
            }
        }
    }
    public int activeCount(long classId,long yearId)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM inscriptions WHERE classe_id=? AND annee_scolaire_id=? AND statut='ACTIVE'")) {
            p.setLong(1,classId);
            p.setLong(2,yearId);
            try(ResultSet r=p.executeQuery()) {
                r.next();
                return r.getInt(1);
            }
        }
    }
    private void bind(PreparedStatement p,Classe o)throws SQLException {
        p.setString(1,o.getNom());
        p.setString(2,o.getNiveau());
        p.setString(3,o.getSection());
        p.setString(4,o.getOption());
        p.setInt(5,o.getCapaciteMax());
        p.setString(6,o.getStatut());
    }
    private Classe map(ResultSet r)throws SQLException {
        Classe o=new Classe();
        o.setId(r.getLong("id"));
        o.setNom(r.getString("nom"));
        o.setNiveau(r.getString("niveau"));
        o.setSection(r.getString("section"));
        o.setOption(r.getString("option"));
        o.setCapaciteMax(r.getInt("capacite_max"));
        o.setStatut(r.getString("statut"));
        o.setEffectif(r.getInt("effectif"));
        Timestamp t=r.getTimestamp("date_creation");
        if(t!=null)o.setDateCreation(t.toLocalDateTime());
        return o;
    }
}
