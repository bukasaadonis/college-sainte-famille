package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Periode;
import java.sql.*;
import java.util.*;
public class PeriodeDAO {
    public List<Periode>findAll()throws SQLException {
        List<Periode>l=new ArrayList<>();
        String s="SELECT p.*,a.libelle annee_scolaire FROM periodes p JOIN annees_scolaires a ON a.id=p.annee_scolaire_id ORDER BY a.date_debut DESC,p.date_debut";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s);ResultSet r=p.executeQuery()) {
            while(r.next())l.add(map(r));
        }
        return l;
    }
    public List<Periode>findByYear(long yearId)throws SQLException {
        List<Periode>l=new ArrayList<>();
        String s="SELECT p.*,a.libelle annee_scolaire FROM periodes p JOIN annees_scolaires a ON a.id=p.annee_scolaire_id WHERE p.annee_scolaire_id=? ORDER BY p.date_debut";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            p.setLong(1,yearId);
            try(ResultSet r=p.executeQuery()) {
                while(r.next())l.add(map(r));
            }
        }
        return l;
    }
    public long insert(Periode o)throws SQLException {
        String s="INSERT INTO periodes(annee_scolaire_id,nom,date_debut,date_fin,type) VALUES(?,?,?,?,?)";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s,Statement.RETURN_GENERATED_KEYS)) {
            bind(p,o);
            p.executeUpdate();
            try(ResultSet r=p.getGeneratedKeys()) {
                r.next();
                return r.getLong(1);
            }
        }
    }
    public void update(Periode o)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("UPDATE periodes SET annee_scolaire_id=?,nom=?,date_debut=?,date_fin=?,type=? WHERE id=?")) {
            bind(p,o);
            p.setLong(6,o.getId());
            p.executeUpdate();
        }
    }
    public void delete(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            try(PreparedStatement check=c.prepareStatement("SELECT (SELECT COUNT(*) FROM notes WHERE periode_id=?) + (SELECT COUNT(*) FROM bulletins WHERE periode_id=?)")) {
                check.setLong(1,id);
                check.setLong(2,id);
                try(ResultSet rs=check.executeQuery()) {
                    rs.next();
                    if(rs.getInt(1)>0)throw new SQLException("Cette période contient des notes ou des bulletins. La suppression est bloquée pour protéger les résultats.");
                }
            }
            try(PreparedStatement p=c.prepareStatement("DELETE FROM periodes WHERE id=?")) {
                p.setLong(1,id);
                p.executeUpdate();
            }
        }
    }
    private void bind(PreparedStatement p,Periode o)throws SQLException {
        p.setLong(1,o.getAnneeScolaireId());
        p.setString(2,o.getNom());
        p.setDate(3,java.sql.Date.valueOf(o.getDateDebut()));
        p.setDate(4,java.sql.Date.valueOf(o.getDateFin()));
        p.setString(5,o.getType());
    }
    private Periode map(ResultSet r)throws SQLException {
        Periode o=new Periode();
        o.setId(r.getLong("id"));
        o.setAnneeScolaireId(r.getLong("annee_scolaire_id"));
        o.setNom(r.getString("nom"));
        o.setDateDebut(r.getDate("date_debut").toLocalDate());
        o.setDateFin(r.getDate("date_fin").toLocalDate());
        o.setType(r.getString("type"));
        o.setAnneeScolaire(r.getString("annee_scolaire"));
        return o;
    }
}
