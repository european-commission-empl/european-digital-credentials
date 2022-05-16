package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import eu.europa.ec.empl.edci.datamodel.Emptiable;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity(name = AwardingProcessDCDAO.TABLE)
@Table(name = AwardingProcessDCDAO.TABLE)
public class AwardingProcessDCDAO implements IGenericDAO, Emptiable {

    public static final String TABLE = "DC_AWARD_PROCESS";
    public static final String TABLE_SHORT = "DC_AWA_PRO";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    /* *************
     *   Fields    *
     ***************/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    @EmptiableIgnore
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private List<IdentifierDTDAO> identifier; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private TextDTDAO description; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TextDTDAO.TABLE_PK_REF))
    private List<TextDTDAO> additionalNote; //*

    @Column(name = "TARGET_RESOURCE")
    private Date awardingDate; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AWARDING_LOCAT_PK", referencedColumnName = "PK")
    private LocationDCDAO awardingLocation; //0..1

    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_LOCAT",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AssessmentSpecDAO.TABLE_PK_REF))
    private Set<AssessmentSpecDAO> used; //*

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_AWARD_BODY",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = OrganizationSpecDAO.TABLE_PK_REF))
    private Set<OrganizationSpecDAO> awardingBody; //1..*

    public List<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public TextDTDAO getDescription() {
        return description;
    }

    public void setDescription(TextDTDAO description) {
        this.description = description;
    }

    public List<TextDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<TextDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public Date getAwardingDate() {
        return awardingDate;
    }

    public void setAwardingDate(Date awardingDate) {
        this.awardingDate = awardingDate;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Set<AssessmentSpecDAO> getUsed() {
        return used;
    }

    public void setUsed(Set<AssessmentSpecDAO> used) {
        this.used = used;
    }

//    public Set<LearningAchievementSpecDAO> getLearningAchievement() {
//        return learningAchievement;
//    }
//
//    public void setLearningAchievement(Set<LearningAchievementSpecDAO> learningAchievement) {
//        this.learningAchievement = learningAchievement;
//    }

    public Set<OrganizationSpecDAO> getAwardingBody() {
        return awardingBody;
    }

    public void setAwardingBody(Set<OrganizationSpecDAO> awardingBody) {
        this.awardingBody = awardingBody;
    }

    public LocationDCDAO getAwardingLocation() {
        return awardingLocation;
    }

    public void setAwardingLocation(LocationDCDAO awardingLocation) {
        this.awardingLocation = awardingLocation;
    }
}