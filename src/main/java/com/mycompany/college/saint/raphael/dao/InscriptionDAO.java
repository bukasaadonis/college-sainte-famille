package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Inscription;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
public class InscriptionDAO {
    public List<Inscription>findAll(String search)throws SQLException {
        List<Inscription>l=new ArrayList<>();
        String s="SELECT i.id,i.eleve_id,i.classe_id,i.annee_scolaire_id,i.num_inscription,i.date_inscription,i.statut,i.observations,CONCAT(e.nom,' ',COALESCE(e.postnom,''),' ',e.prenom) eleve_nom,e.matricule,c.nom classe_nom,a.libelle annee_scolaire FROM inscriptions i JOIN eleves e ON e.id=i.eleve_id JOIN classes c ON c.id=i.classe_id JOIN annees_scolaires a ON a.id=i.annee_scolaire_id WHERE (?='' OR i.num_inscription LIKE ? OR e.matricule LIKE ? OR e.nom LIKE ? OR c.nom LIKE ?) ORDER BY i.date_inscription DESC,i.id DESC";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            String q=search==null?"":search.trim(),x="%"+q+"%";
            p.setString(1,q);
            for(int i=2;i<=5;i++)p.setString(i,x);
            try(ResultSet r=p.executeQuery()) {
                while(r.next())l.add(map(r));
            }
        }
        return l;
    }
    public String generateNumber(Connection c)throws SQLException {
        String year = String.valueOf(java.time.LocalDate.now().getYear());
        String pattern = "INS-" + year + "-%";
        int sequence = 1;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COALESCE(MAX(CAST(RIGHT(num_inscription,3) AS UNSIGNED)),0)+1 FROM inscriptions WHERE num_inscription LIKE ?")) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) sequence = rs.getInt(1);
            }
        }
        return "INS-" + year + "-" + String.format("%03d", sequence);
    }
    public long insert(Connection c,Inscription o)throws SQLException {
        String s="INSERT INTO inscriptions(eleve_id,classe_id,annee_scolaire_id,num_inscription,date_inscription,statut,observations) VALUES(?,?,?,?,?,?,?)";
        try(PreparedStatement p=c.prepareStatement(s,Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1,o.getEleveId());
            p.setLong(2,o.getClasseId());
            p.setLong(3,o.getAnneeScolaireId());
            p.setString(4,o.getNumInscription());
            p.setDate(5,java.sql.Date.valueOf(o.getDateInscription()==null?LocalDate.now():o.getDateInscription()));
            p.setString(6,o.getStatut());
            p.setString(7,o.getObservations());
            p.executeUpdate();
            try(ResultSet r=p.getGeneratedKeys()) {
                r.next();
                return r.getLong(1);
            }
        }
    }
    public long insert(Inscription o)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            o.setNumInscription(generateNumber(c));
            return insert(c,o);
        }
    }
    public void cancel(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("UPDATE inscriptions SET statut='ANNULEE' WHERE id=?")) {
            p.setLong(1,id);
            p.executeUpdate();
        }
    }
    public boolean existsActive(long studentId,long yearId)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("SELECT 1 FROM inscriptions WHERE eleve_id=? AND annee_scolaire_id=? AND statut='ACTIVE' LIMIT 1")) {
            p.setLong(1,studentId);
            p.setLong(2,yearId);
            try(ResultSet r=p.executeQuery()) {
                return r.next();
            }
        }
    }
    private Inscription map(ResultSet r)throws SQLException {
        Inscription o=new Inscription();
        o.setId(r.getLong("id"));
        o.setEleveId(r.getLong("eleve_id"));
        o.setClasseId(r.getLong("classe_id"));
        o.setAnneeScolaireId(r.getLong("annee_scolaire_id"));
        o.setNumInscription(r.getString("num_inscription"));
        o.setDateInscription(r.getDate("date_inscription").toLocalDate());
        o.setStatut(r.getString("statut"));
        o.setObservations(r.getString("observations"));
        o.setEleveNom(r.getString("eleve_nom").replaceAll("\\s+"," ").trim());
        o.setMatricule(r.getString("matricule"));
        o.setClasseNom(r.getString("classe_nom"));
        o.setAnneeScolaire(r.getString("annee_scolaire"));
        return o;
    }
}
