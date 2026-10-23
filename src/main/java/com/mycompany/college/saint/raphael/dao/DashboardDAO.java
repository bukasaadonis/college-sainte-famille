package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Eleve;
import java.sql.*;
import java.util.*;
public class DashboardDAO {
    public Map<String,Integer>stats()throws SQLException {
        Map<String,Integer>m=new LinkedHashMap<>();
        String sql="SELECT "
            + "(SELECT COUNT(*) FROM eleves WHERE supprime=0 AND statut='ACTIF') total_eleves,"
            + "(SELECT COUNT(*) FROM eleves WHERE supprime=0 AND sexe='MASCULIN') total_garcons,"
            + "(SELECT COUNT(*) FROM eleves WHERE supprime=0 AND sexe='FEMININ') total_filles,"
            + "(SELECT COUNT(*) FROM classes WHERE statut='ACTIVE') total_classes,"
            + "(SELECT COUNT(*) FROM inscriptions WHERE statut='ACTIVE' AND annee_scolaire_id=(SELECT id FROM annees_scolaires WHERE est_active=1 ORDER BY date_debut DESC LIMIT 1)) total_inscriptions,"
            + "(SELECT COUNT(*) FROM utilisateurs WHERE statut='ACTIF') total_utilisateurs";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()) {
            if(r.next()) {
                m.put("eleves",r.getInt("total_eleves"));
                m.put("garcons",r.getInt("total_garcons"));
                m.put("filles",r.getInt("total_filles"));
                m.put("classes",r.getInt("total_classes"));
                m.put("inscriptions",r.getInt("total_inscriptions"));
                m.put("utilisateurs",r.getInt("total_utilisateurs"));
            }
        }
        return m;
    }
    public Map<String,Integer>enrollmentByClass()throws SQLException {
        Map<String,Integer>m=new LinkedHashMap<>();
        String s="SELECT c.nom,COUNT(i.id) total FROM classes c LEFT JOIN inscriptions i ON i.classe_id=c.id AND i.statut='ACTIVE' AND i.annee_scolaire_id=(SELECT id FROM annees_scolaires WHERE est_active=1 ORDER BY date_debut DESC LIMIT 1) WHERE c.statut='ACTIVE' GROUP BY c.id ORDER BY c.nom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s);ResultSet r=p.executeQuery()) {
            while(r.next())m.put(r.getString(1),r.getInt(2));
        }
        return m;
    }
    public List<Eleve>recentStudents()throws SQLException {
        return new EleveDAO().findAll("").stream().sorted(Comparator.comparing(Eleve::getDateCreation,Comparator.nullsLast(Comparator.reverseOrder()))).limit(6).toList();
    }
}
