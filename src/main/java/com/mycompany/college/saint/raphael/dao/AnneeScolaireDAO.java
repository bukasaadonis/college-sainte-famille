package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.AnneeScolaire;
import java.sql.*;
import java.util.*;
public class AnneeScolaireDAO {
    public List<AnneeScolaire>findAll()throws SQLException {
        List<AnneeScolaire>l=new ArrayList<>();
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM annees_scolaires ORDER BY date_debut DESC");ResultSet r=p.executeQuery()) {
            while(r.next())l.add(map(r));
        }
        return l;
    }
    public AnneeScolaire findActive()throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM annees_scolaires WHERE est_active=1 ORDER BY date_debut DESC LIMIT 1");ResultSet r=p.executeQuery()) {
            return r.next()?map(r):null;
        }
    }
    public long insert(AnneeScolaire o)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO annees_scolaires(libelle,date_debut,date_fin,est_active) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1,o.getLibelle());
            p.setDate(2,java.sql.Date.valueOf(o.getDateDebut()));
            p.setDate(3,java.sql.Date.valueOf(o.getDateFin()));
            p.setBoolean(4,o.isActive());
            p.executeUpdate();
            try(ResultSet r=p.getGeneratedKeys()) {
                r.next();
                return r.getLong(1);
            }
        }
    }
    public void update(AnneeScolaire o)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement("UPDATE annees_scolaires SET libelle=?,date_debut=?,date_fin=? WHERE id=?")) {
            p.setString(1,o.getLibelle());
            p.setDate(2,java.sql.Date.valueOf(o.getDateDebut()));
            p.setDate(3,java.sql.Date.valueOf(o.getDateFin()));
            p.setLong(4,o.getId());
            p.executeUpdate();
        }
    }
    public void activate(long id)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try(Statement s=c.createStatement()) {
                s.executeUpdate("UPDATE annees_scolaires SET est_active=0");
            }
            try(PreparedStatement p=c.prepareStatement("UPDATE annees_scolaires SET est_active=1 WHERE id=?")) {
                p.setLong(1,id);
                p.executeUpdate();
            }
            c.commit();
        }
    }
    private AnneeScolaire map(ResultSet r)throws SQLException {
        AnneeScolaire o=new AnneeScolaire();
        o.setId(r.getLong("id"));
        o.setLibelle(r.getString("libelle"));
        o.setDateDebut(r.getDate("date_debut").toLocalDate());
        o.setDateFin(r.getDate("date_fin").toLocalDate());
        o.setActive(r.getBoolean("est_active"));
        return o;
    }
}
