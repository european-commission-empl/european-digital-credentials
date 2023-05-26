package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;

@Entity(name = ContactPointDCDAO.TABLE)
@Table(name = ContactPointDCDAO.TABLE)
public class ContactPointDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_CONTACT_POINT";
    public static final String TABLE_SHORT = "DC_CON_POI";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private List<NoteDTDAO> additionalNote; //0..*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_TEXT_PK", referencedColumnName = "PK")
    private TextDTDAO description; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_POST_ADDR",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AddressDCDAO.TABLE_PK_REF))
    private List<AddressDCDAO> address; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_PHONE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = PhoneDCDAO.TABLE_PK_REF))
    private List<PhoneDCDAO> phone; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_EMAIL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = MailboxDCDAO.TABLE_PK_REF))
    private List<MailboxDCDAO> emailAddress; //1..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "CONTACT_FORM",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> contactForm; //0..*

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public TextDTDAO getDescription() {
        return description;
    }

    public void setDescription(TextDTDAO description) {
        this.description = description;
    }

    public List<AddressDCDAO> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDCDAO> address) {
        this.address = address;
    }

    public List<PhoneDCDAO> getPhone() {
        return phone;
    }

    public void setPhone(List<PhoneDCDAO> phone) {
        this.phone = phone;
    }

    public List<MailboxDCDAO> getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(List<MailboxDCDAO> emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<WebDocumentDCDAO> getContactForm() {
        return contactForm;
    }

    public void setContactForm(List<WebDocumentDCDAO> contactForm) {
        this.contactForm = contactForm;
    }
}
