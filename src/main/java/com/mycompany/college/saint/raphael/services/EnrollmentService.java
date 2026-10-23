package com.mycompany.college.saint.raphael.services;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
public class EnrollmentService {
    private final EleveDAO eleveDAO=new EleveDAO();
    private final InscriptionDAO inscriptionDAO=new InscriptionDAO();
    private final ClasseDAO classeDAO=new ClasseDAO();
    public long createStudentAndEnroll(Eleve e,Classe classe,AnneeScolaire year,String observations)throws SQLException {
        if(classeDAO.activeCount(classe.getId(),year.getId())>=classe.getCapaciteMax())throw new IllegalArgumentException("La capacité maximale de la classe est atteinte.");
        try(Connection c=DatabaseConfig.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                e.setMatricule(eleveDAO.generateMatricule(c,classe.getNom()));
                long studentId=eleveDAO.insert(c,e);
                Inscription i=new Inscription();
                i.setEleveId(studentId);
                i.setClasseId(classe.getId());
                i.setAnneeScolaireId(year.getId());
                i.setNumInscription(inscriptionDAO.generateNumber(c));
                i.setDateInscription(LocalDate.now());
                i.setStatut("ACTIVE");
                i.setObservations(observations);
                long enrollmentId=inscriptionDAO.insert(c,i);
                c.commit();
                AuditService.getInstance().log("CREATION","ELEVE",studentId,"Nouvel élève "+e.getMatricule()+" inscrit en "+classe.getNom());
                return enrollmentId;
            }
            catch(Exception ex) {
                c.rollback();
                if(ex instanceof SQLException se)throw se;
                if(ex instanceof RuntimeException re)throw re;
                throw new SQLException(ex);
            }
        }
    }
    public long enrollExisting(Eleve e,Classe classe,AnneeScolaire year,LocalDate enrollmentDate,String observations)throws SQLException {
        if(inscriptionDAO.existsActive(e.getId(),year.getId()))throw new IllegalArgumentException("Cet élève possède déjà une inscription active pour cette année.");
        if(classeDAO.activeCount(classe.getId(),year.getId())>=classe.getCapaciteMax())throw new IllegalArgumentException("La capacité maximale de la classe est atteinte.");
        Inscription i=new Inscription();
        i.setEleveId(e.getId());
        i.setClasseId(classe.getId());
        i.setAnneeScolaireId(year.getId());
        i.setDateInscription(enrollmentDate==null?LocalDate.now():enrollmentDate);
        i.setStatut("ACTIVE");
        i.setObservations(observations);
        long id=inscriptionDAO.insert(i);
        AuditService.getInstance().log("CREATION","INSCRIPTION",id,e.getMatricule()+" inscrit en "+classe.getNom());
        return id;
    }
}
