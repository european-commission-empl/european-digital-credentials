package eu.europa.ec.empl.edci.repository.entity;

public interface IAuditedDAO {

    public IAuditDAO getAuditDAO();

    public void setAuditDAO(IAuditDAO auditDAO);

    public Long getPk();

}
