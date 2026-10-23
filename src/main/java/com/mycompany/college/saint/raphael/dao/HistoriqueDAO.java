package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Historique;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
public class HistoriqueDAO {
    public List<Historique>find(String search,LocalDate from,LocalDate to)throws SQLException {
        List<Historique>l=new ArrayList<>();
        String s="SELECT h.*,CONCAT(u.nom,' ',u.prenom) utilisateur_nom FROM historiques h JOIN utilisateurs u ON u.id=h.utilisateur_id WHERE (?='' OR h.action LIKE ? OR h.entite LIKE ? OR h.details LIKE ? OR u.nom LIKE ?) AND (? IS NULL OR DATE(h.date_action)>=?) AND (? IS NULL OR DATE(h.date_action)<=?) ORDER BY h.date_action DESC LIMIT 1000";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            String q=search==null?"":search.trim(),x="%"+q+"%";
            p.setString(1,q);
            for(int i=2;i<=5;i++)p.setString(i,x);
            if(from==null) {
                p.setNull(6,Types.DATE);
                p.setNull(7,Types.DATE);
            }
            else {
                p.setDate(6,java.sql.Date.valueOf(from));
                p.setDate(7,java.sql.Date.valueOf(from));
            }
            if(to==null) {
                p.setNull(8,Types.DATE);
                p.setNull(9,Types.DATE);
            }
            else {
                p.setDate(8,java.sql.Date.valueOf(to));
                p.setDate(9,java.sql.Date.valueOf(to));
            }
            try(ResultSet r=p.executeQuery()) {
                while(r.next()) {
                    Historique o=new Historique();
                    o.setId(r.getLong("id"));
                    o.setUtilisateurId(r.getLong("utilisateur_id"));
                    o.setUtilisateurNom(r.getString("utilisateur_nom"));
                    o.setAction(r.getString("action"));
                    o.setEntite(r.getString("entite"));
                    long eid=r.getLong("entite_id");
                    o.setEntiteId(r.wasNull()?null:eid);
                    o.setDetails(r.getString("details"));
                    Timestamp t=r.getTimestamp("date_action");
                    if(t!=null)o.setDateAction(t.toLocalDateTime());
                    o.setAdresseIp(r.getString("adresse_ip"));
                    l.add(o);
                }
            }
        }
        return l;
    }
}
