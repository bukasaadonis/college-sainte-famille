package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Eleve;
import java.sql.*;
import java.util.*;
public class EleveDAO {
    public List<Eleve> findAll(String search)throws SQLException {
        List<Eleve> list=new ArrayList<>();
        String sql="SELECT e.*,c.nom classe_nom,a.libelle annee_scolaire,i.num_inscription FROM eleves e LEFT JOIN inscriptions i ON i.eleve_id=e.id AND i.statut='ACTIVE' AND i.annee_scolaire_id=(SELECT id FROM annees_scolaires WHERE est_active=1 ORDER BY date_debut DESC LIMIT 1) LEFT JOIN classes c ON c.id=i.classe_id LEFT JOIN annees_scolaires a ON a.id=i.annee_scolaire_id WHERE e.supprime=0 AND (?='' OR e.matricule LIKE ? OR e.nom LIKE ? OR e.postnom LIKE ? OR e.prenom LIKE ?) ORDER BY e.nom,e.postnom,e.prenom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            String q=search==null?"":search.trim(),like="%"+q+"%";
            ps.setString(1,q);
            for(int i=2;i<=5;i++)ps.setString(i,like);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next())list.add(mapView(rs));
            }
        }
        return list;
    }
    public List<Eleve> findActiveByClass(long classId,long yearId)throws SQLException {
        List<Eleve> list=new ArrayList<>();
        String sql="SELECT e.* FROM eleves e JOIN inscriptions i ON i.eleve_id=e.id WHERE i.classe_id=? AND i.annee_scolaire_id=? AND i.statut='ACTIVE' AND e.supprime=0 ORDER BY e.nom,e.prenom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,classId);
            ps.setLong(2,yearId);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next())list.add(mapBase(rs));
            }
        }
        return list;
    }
    public List<Eleve> findAvailableForYear(long yearId)throws SQLException {
        List<Eleve> list=new ArrayList<>();
        String sql="SELECT e.* FROM eleves e WHERE e.supprime=0 AND e.statut='ACTIF' AND NOT EXISTS(SELECT 1 FROM inscriptions i WHERE i.eleve_id=e.id AND i.annee_scolaire_id=? AND i.statut='ACTIVE') ORDER BY e.nom,e.prenom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,yearId);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next())list.add(mapBase(rs));
            }
        }
        return list;
    }
    public String generateMatricule(Connection c,String className)throws SQLException {
        String year = String.valueOf(java.time.LocalDate.now().getYear());
        String prefix = className == null ? "CLASSE" : className.replaceAll("\\s+", "");
        if (prefix.length() > 10) prefix = prefix.substring(0, 10);
        String pattern = year + "-" + prefix + "-%";
        int sequence = 1;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COALESCE(MAX(CAST(RIGHT(matricule,3) AS UNSIGNED)),0)+1 FROM eleves WHERE matricule LIKE ?")) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) sequence = rs.getInt(1);
            }
        }
        return year + "-" + prefix + "-" + String.format("%03d", sequence);
    }
    public long insert(Connection c,Eleve e)throws SQLException {
        String sql="INSERT INTO eleves(matricule,nom,postnom,prenom,sexe,date_naissance,lieu_naissance,nationalite,adresse,telephone,email,photo,nom_pere,nom_mere,etat_civil_parents,etablissement_provenance,statut,supprime) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)";
        try(PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            bind(ps,e);
            ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()) {
                if(rs.next())return rs.getLong(1);
            }
            throw new SQLException("ID élève non généré");
        }
    }
    public void update(Eleve e)throws SQLException {
        String sql="UPDATE eleves SET nom=?,postnom=?,prenom=?,sexe=?,date_naissance=?,lieu_naissance=?,nationalite=?,adresse=?,telephone=?,email=?,photo=?,nom_pere=?,nom_mere=?,etat_civil_parents=?,etablissement_provenance=?,statut=?,date_modification=NOW() WHERE id=?";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            int i=1;
            ps.setString(i++,e.getNom());
            ps.setString(i++,e.getPostnom());
            ps.setString(i++,e.getPrenom());
            ps.setString(i++,e.getSexe());
            ps.setDate(i++,java.sql.Date.valueOf(e.getDateNaissance()));
            ps.setString(i++,e.getLieuNaissance());
            ps.setString(i++,e.getNationalite());
            ps.setString(i++,e.getAdresse());
            ps.setString(i++,e.getTelephone());
            ps.setString(i++,e.getEmail());
            if(e.getPhoto()==null)ps.setNull(i++,Types.BLOB);
            else ps.setBytes(i++,e.getPhoto());
            ps.setString(i++,e.getNomPere());
            ps.setString(i++,e.getNomMere());
            ps.setString(i++,e.getEtatCivilParents());
            ps.setString(i++,e.getEtablissementProvenance());
            ps.setString(i++,e.getStatut());
            ps.setLong(i,e.getId());
            ps.executeUpdate();
        }
    }
    public void softDelete(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try(PreparedStatement p1=c.prepareStatement("UPDATE inscriptions SET statut='ANNULEE' WHERE eleve_id=? AND statut='ACTIVE'");PreparedStatement p2=c.prepareStatement("UPDATE eleves SET supprime=1,statut='INACTIF',date_modification=NOW() WHERE id=?")) {
                p1.setLong(1,id);
                p1.executeUpdate();
                p2.setLong(1,id);
                p2.executeUpdate();
                c.commit();
            }
            catch(SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }
    private void bind(PreparedStatement ps,Eleve e)throws SQLException {
        int i=1;
        ps.setString(i++,e.getMatricule());
        ps.setString(i++,e.getNom());
        ps.setString(i++,e.getPostnom());
        ps.setString(i++,e.getPrenom());
        ps.setString(i++,e.getSexe());
        ps.setDate(i++,java.sql.Date.valueOf(e.getDateNaissance()));
        ps.setString(i++,e.getLieuNaissance());
        ps.setString(i++,e.getNationalite());
        ps.setString(i++,e.getAdresse());
        ps.setString(i++,e.getTelephone());
        ps.setString(i++,e.getEmail());
        if(e.getPhoto()==null)ps.setNull(i++,Types.BLOB);
        else ps.setBytes(i++,e.getPhoto());
        ps.setString(i++,e.getNomPere());
        ps.setString(i++,e.getNomMere());
        ps.setString(i++,e.getEtatCivilParents());
        ps.setString(i++,e.getEtablissementProvenance());
        ps.setString(i,e.getStatut());
    }
    private Eleve mapView(ResultSet rs)throws SQLException {
        Eleve e=mapBase(rs);
        e.setClasseNom(rs.getString("classe_nom"));
        e.setAnneeScolaire(rs.getString("annee_scolaire"));
        e.setNumInscription(rs.getString("num_inscription"));
        return e;
    }
    private Eleve mapBase(ResultSet rs)throws SQLException {
        Eleve e=new Eleve();
        e.setId(rs.getLong("id"));
        e.setMatricule(rs.getString("matricule"));
        e.setNom(rs.getString("nom"));
        e.setPostnom(rs.getString("postnom"));
        e.setPrenom(rs.getString("prenom"));
        e.setSexe(rs.getString("sexe"));
        java.sql.Date d=rs.getDate("date_naissance");
        if(d!=null)e.setDateNaissance(d.toLocalDate());
        e.setLieuNaissance(rs.getString("lieu_naissance"));
        e.setNationalite(rs.getString("nationalite"));
        e.setAdresse(rs.getString("adresse"));
        e.setTelephone(rs.getString("telephone"));
        e.setEmail(rs.getString("email"));
        e.setPhoto(rs.getBytes("photo"));
        e.setNomPere(rs.getString("nom_pere"));
        e.setNomMere(rs.getString("nom_mere"));
        e.setEtatCivilParents(rs.getString("etat_civil_parents"));
        e.setEtablissementProvenance(rs.getString("etablissement_provenance"));
        e.setStatut(rs.getString("statut"));
        e.setSupprime(rs.getBoolean("supprime"));
        Timestamp t=rs.getTimestamp("date_creation");
        if(t!=null)e.setDateCreation(t.toLocalDateTime());
        return e;
    }
}
