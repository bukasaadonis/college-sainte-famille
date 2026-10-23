package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.NoteRow;
import java.sql.*;
import java.util.*;
public class NoteDAO {
    public List<NoteRow>loadRows(long classId,long yearId,long courseId,long periodId)throws SQLException {
        List<NoteRow>l=new ArrayList<>();
        String s="SELECT e.id eleve_id,e.matricule,CONCAT(e.nom,' ',COALESCE(e.postnom,''),' ',e.prenom) eleve_nom,n.valeur,COALESCE(n.maximum,20) maximum,COALESCE(n.coefficient,c.coefficient) coefficient FROM inscriptions i JOIN eleves e ON e.id=i.eleve_id JOIN cours c ON c.id=? AND c.classe_id=i.classe_id LEFT JOIN notes n ON n.eleve_id=e.id AND n.cours_id=c.id AND n.periode_id=? WHERE i.classe_id=? AND i.annee_scolaire_id=? AND i.statut='ACTIVE' AND e.supprime=0 ORDER BY e.nom,e.prenom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            p.setLong(1,courseId);
            p.setLong(2,periodId);
            p.setLong(3,classId);
            p.setLong(4,yearId);
            try(ResultSet r=p.executeQuery()) {
                while(r.next()) {
                    NoteRow o=new NoteRow();
                    o.setEleveId(r.getLong("eleve_id"));
                    o.setMatricule(r.getString("matricule"));
                    o.setEleveNom(r.getString("eleve_nom").replaceAll("\\s+"," ").trim());
                    double v=r.getDouble("valeur");
                    o.setValeur(r.wasNull()?null:v);
                    o.setMaximum(r.getDouble("maximum"));
                    o.setCoefficient(r.getDouble("coefficient"));
                    l.add(o);
                }
            }
        }
        return l;
    }
    public void saveAll(long courseId,long periodId,List<NoteRow>rows)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            c.setAutoCommit(false);
            String find="SELECT id FROM notes WHERE eleve_id=? AND cours_id=? AND periode_id=? LIMIT 1";
            String ins="INSERT INTO notes(eleve_id,cours_id,periode_id,valeur,maximum,coefficient) VALUES(?,?,?,?,?,?)";
            String upd="UPDATE notes SET valeur=?,maximum=?,coefficient=?,date_saisie=NOW() WHERE id=?";
            try(PreparedStatement pf=c.prepareStatement(find);PreparedStatement pi=c.prepareStatement(ins);PreparedStatement pu=c.prepareStatement(upd)) {
                for(NoteRow r:rows) {
                    if(r.getValeur()==null)continue;
                    pf.setLong(1,r.getEleveId());
                    pf.setLong(2,courseId);
                    pf.setLong(3,periodId);
                    Long id=null;
                    try(ResultSet rs=pf.executeQuery()) {
                        if(rs.next())id=rs.getLong(1);
                    }
                    if(id==null) {
                        pi.setLong(1,r.getEleveId());
                        pi.setLong(2,courseId);
                        pi.setLong(3,periodId);
                        pi.setDouble(4,r.getValeur());
                        pi.setDouble(5,r.getMaximum());
                        pi.setDouble(6,r.getCoefficient());
                        pi.addBatch();
                    }
                    else {
                        pu.setDouble(1,r.getValeur());
                        pu.setDouble(2,r.getMaximum());
                        pu.setDouble(3,r.getCoefficient());
                        pu.setLong(4,id);
                        pu.addBatch();
                    }
                }
                pi.executeBatch();
                pu.executeBatch();
                c.commit();
            }
            catch(SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
