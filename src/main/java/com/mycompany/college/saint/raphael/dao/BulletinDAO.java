package com.mycompany.college.saint.raphael.dao;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Bulletin;
import java.sql.*;
import java.util.*;
public class BulletinDAO {
    public List<Bulletin>calculate(long classId,long yearId,long periodId)throws SQLException {
        List<Bulletin>l=new ArrayList<>();
        String s="SELECT e.id eleve_id,e.matricule,CONCAT(e.nom,' ',COALESCE(e.postnom,''),' ',e.prenom) eleve_nom,ROUND(SUM(CASE WHEN cr.id IS NOT NULL THEN (n.valeur/n.maximum)*20*n.coefficient ELSE 0 END)/NULLIF(SUM(CASE WHEN cr.id IS NOT NULL THEN n.coefficient ELSE 0 END),0),2) moyenne FROM inscriptions i JOIN eleves e ON e.id=i.eleve_id LEFT JOIN notes n ON n.eleve_id=e.id AND n.periode_id=? LEFT JOIN cours cr ON cr.id=n.cours_id AND cr.classe_id=i.classe_id WHERE i.classe_id=? AND i.annee_scolaire_id=? AND i.statut='ACTIVE' AND e.supprime=0 GROUP BY e.id ORDER BY moyenne DESC,e.nom";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement p=c.prepareStatement(s)) {
            p.setLong(1,periodId);
            p.setLong(2,classId);
            p.setLong(3,yearId);
            try(ResultSet r=p.executeQuery()) {
                int rank=0;
                double previous=Double.NaN;
                int pos=0;
                while(r.next()) {
                    pos++;
                    double avg=r.getDouble("moyenne");
                    if(r.wasNull())avg=0;
                    if(Double.isNaN(previous)||Double.compare(avg,previous)!=0)rank=pos;
                    previous=avg;
                    Bulletin b=new Bulletin();
                    b.setEleveId(r.getLong("eleve_id"));
                    b.setPeriodeId(periodId);
                    b.setMatricule(r.getString("matricule"));
                    b.setEleveNom(r.getString("eleve_nom").replaceAll("\\s+"," ").trim());
                    b.setMoyenneGenerale(avg);
                    b.setRang(rank);
                    b.setDecision(avg>=10?"ADMIS":"AJOURNÉ");
                    l.add(b);
                }
            }
        }
        return l;
    }
    public void saveAll(List<Bulletin>list,long periodId)throws SQLException {
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try(PreparedStatement del=c.prepareStatement("DELETE FROM bulletins WHERE periode_id=? AND eleve_id=?");PreparedStatement ins=c.prepareStatement("INSERT INTO bulletins(eleve_id,periode_id,moyenne_generale,rang,decision,date_emission) VALUES(?,?,?,?,?,NOW())")) {
                for(Bulletin b:list) {
                    del.setLong(1,periodId);
                    del.setLong(2,b.getEleveId());
                    del.addBatch();
                }
                del.executeBatch();
                for(Bulletin b:list) {
                    ins.setLong(1,b.getEleveId());
                    ins.setLong(2,periodId);
                    ins.setDouble(3,b.getMoyenneGenerale());
                    ins.setInt(4,b.getRang());
                    ins.setString(5,b.getDecision());
                    ins.addBatch();
                }
                ins.executeBatch();
                c.commit();
            }
            catch(SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
