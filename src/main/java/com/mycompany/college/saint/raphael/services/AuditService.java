package com.mycompany.college.saint.raphael.services;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.utils.SessionManager;
import java.sql.*;
public final class AuditService {
    private static final AuditService INSTANCE=new AuditService();
    private AuditService() {
    }
    public static AuditService getInstance() {
        return INSTANCE;
    }
    public void log(String action,String entity,Long entityId,String details) {
        Long uid=SessionManager.getInstance().getCurrentUserId();
        if(uid==null)return;
        String sql="INSERT INTO historiques(utilisateur_id,action,entite,entite_id,details,adresse_ip) VALUES(?,?,?,?,?,?)";
        try(Connection c=DatabaseConfig.getInstance().getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,uid);
            ps.setString(2,action);
            ps.setString(3,entity);
            if(entityId==null)ps.setNull(4,Types.BIGINT);
            else ps.setLong(4,entityId);
            ps.setString(5,details);
            ps.setString(6,"LOCAL");
            ps.executeUpdate();
        }
        catch(SQLException ignored) {
        }
    }
}
