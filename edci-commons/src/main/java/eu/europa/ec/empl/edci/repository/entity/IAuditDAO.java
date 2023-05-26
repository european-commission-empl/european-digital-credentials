package eu.europa.ec.empl.edci.repository.entity;

import java.util.Date;

public interface IAuditDAO extends IGenericDAO {

    public Long getPk();

    public void setPk(Long pk);

    public Date getCreateDate();

    public void setCreateDate(Date createDate);

    public Date getUpdateDate();

    public void setUpdateDate(Date updateDate);

    public String getCreateUserId();

    public void setCreateUserId(String createUserId);

    public String getUpdateUserId();

    public void setUpdateUserId(String updateUserId);

}

