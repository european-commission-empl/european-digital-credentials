package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import eu.europa.ec.empl.edci.datamodel.Emptiable;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * The type Global history dao.
 */
@Entity(name = EntitlemSpecificationDCDAO.TABLE)
@Table(name = EntitlemSpecificationDCDAO.TABLE)
public class EntitlemSpecificationDCDAO implements IGenericDAO, Emptiable {

    public static final String TABLE = "DC_ENTITLEM_SPEC";
    public static final String TABLE_SHORT = "DC_ENTITL_S";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    @EmptiableIgnore
    private Long pk;

    /* *************
     *   Fields    *
     ***************/

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIFIER",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private List<IdentifierDTDAO> identifier; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TYPE_PK", referencedColumnName = "PK")
    private CodeDTDAO entitlementType; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ALTER_LABEL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TextDTDAO.TABLE_PK_REF))
    private List<TextDTDAO> alternativeLabel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "STATUS_PK", referencedColumnName = "PK")
    private CodeDTDAO status; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADDI_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private List<NoteDTDAO> additionalNote; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HOME_PAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> homePage; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SUPPL_DOC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> supplementaryDocument; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LIMIT_JUR",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitJurisdiction; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LIMIT_OCC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitOccupation; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LIMIT_N_OCC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitNationalOccupation; //*  OccupationAssociationDTDAO

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_MAY_RES_FROM",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningSpecificationDCDAO.TABLE_PK_REF))
    private List<LearningSpecificationDCDAO> mayResultFrom; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlemSpecificationDCDAO.TABLE_PK_REF + "_SUB"))
    private List<EntitlemSpecificationDCDAO> hasPart; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SPECIAL_OF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlemSpecificationDCDAO.TABLE_PK_REF + "_SUB"))
    private List<EntitlemSpecificationDCDAO> specializationOf; //*


    /* *************
     *  Relations  *
     ***************/


    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LIMIT_ORG",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = OrganizationSpecDAO.TABLE_PK_REF))
    private Set<OrganizationSpecDAO> limitOrganization; //*

    public EntitlemSpecificationDCDAO() {

    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public List<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public CodeDTDAO getEntitlementType() {
        return entitlementType;
    }

    public void setEntitlementType(CodeDTDAO entitlementType) {
        this.entitlementType = entitlementType;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public List<TextDTDAO> getAlternativeLabel() {
        return alternativeLabel;
    }

    public void setAlternativeLabel(List<TextDTDAO> alternativeLabel) {
        this.alternativeLabel = alternativeLabel;
    }

    public CodeDTDAO getStatus() {
        return status;
    }

    public void setStatus(CodeDTDAO status) {
        this.status = status;
    }

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<WebDocumentDCDAO> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDCDAO> homePage) {
        this.homePage = homePage;
    }

    public List<WebDocumentDCDAO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCDAO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public List<CodeDTDAO> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<CodeDTDAO> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public List<CodeDTDAO> getLimitOccupation() {
        return limitOccupation;
    }

    public void setLimitOccupation(List<CodeDTDAO> limitOccupation) {
        this.limitOccupation = limitOccupation;
    }

    public List<CodeDTDAO> getLimitNationalOccupation() {
        return limitNationalOccupation;
    }

    public void setLimitNationalOccupation(List<CodeDTDAO> limitNationalOccupation) {
        this.limitNationalOccupation = limitNationalOccupation;
    }

    public List<LearningSpecificationDCDAO> getMayResultFrom() {
        return mayResultFrom;
    }

    public void setMayResultFrom(List<LearningSpecificationDCDAO> mayResultFrom) {
        this.mayResultFrom = mayResultFrom;
    }

    public List<EntitlemSpecificationDCDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<EntitlemSpecificationDCDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public List<EntitlemSpecificationDCDAO> getSpecializationOf() {
        return specializationOf;
    }

    public void setSpecializationOf(List<EntitlemSpecificationDCDAO> specializationOf) {
        this.specializationOf = specializationOf;
    }

    public Set<OrganizationSpecDAO> getLimitOrganization() {
        return limitOrganization;
    }

    public void setLimitOrganization(Set<OrganizationSpecDAO> limitOrganization) {
        this.limitOrganization = limitOrganization;
    }
}

