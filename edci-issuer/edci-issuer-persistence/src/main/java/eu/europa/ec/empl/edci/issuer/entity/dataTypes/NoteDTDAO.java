package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.datamodel.IReferenced;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableEntity;
import eu.europa.ec.empl.edci.model.base.ILocalizable;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;

@Entity(name = NoteDTDAO.TABLE)
@Table(name = NoteDTDAO.TABLE)
@CustomizableEntity(identifierField = "subject.targetName", entityCode = "NOTE")
public class NoteDTDAO implements IGenericDAO, ILocalizable<ContentDTDAO>, IReferenced {

    public static final String TABLE = "DT_NOTE";
    public static final String TABLE_SHORT = "DT_NOTE";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CONTENTS",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ContentDTDAO.TABLE_PK_REF))
    private List<ContentDTDAO> contents; //?

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SUBJECT_PK", referencedColumnName = "PK")
    private CodeDTDAO subject; //0..1

    @Column(name = "IS_MORE_INFORMATION")
    private Boolean isMoreInformation = false;

    public List<ContentDTDAO> getContents() {
        return contents;
    }

    @Override
    public ContentDTDAO getContentsNewInstance() {
        return new ContentDTDAO();
    }

    public void setContents(List<ContentDTDAO> contents) {
        this.contents = contents;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public CodeDTDAO getSubject() {
        return subject;
    }

    public void setSubject(CodeDTDAO subject) {
        this.subject = subject;
    }

    public String getSubjectLabel() {
        return (subject != null && subject.getTargetName() != null && subject.getTargetName().toString() != null) ? subject.getTargetName().toString() : "";
    }

    @Override
    public Object getReferenced() {
        return this.getSubject();
    }

    public Boolean getMoreInformation() {
        return isMoreInformation;
    }

    public void setMoreInformation(Boolean moreInformation) {
        isMoreInformation = moreInformation;
    }

}