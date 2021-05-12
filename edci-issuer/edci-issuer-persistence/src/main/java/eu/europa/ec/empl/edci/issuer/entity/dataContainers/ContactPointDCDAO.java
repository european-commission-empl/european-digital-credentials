package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private List<NoteDTDAO> note;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_POST_ADDR",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AddressDCDAO.TABLE_PK_REF))
    private List<AddressDCDAO> postalAddress;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_PHONE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = PhoneDCDAO.TABLE_PK_REF))
    private List<PhoneDCDAO> phone;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_EMAIL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = MailboxDCDAO.TABLE_PK_REF))
    private List<MailboxDCDAO> email;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "CONTACT_FORM",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> contactForm;

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public List<NoteDTDAO> getNote() {
        return note;
    }

    public void setNote(List<NoteDTDAO> note) {
        this.note = note;
    }

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public List<AddressDCDAO> getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(List<AddressDCDAO> postalAddress) {
        this.postalAddress = postalAddress;
    }

    public List<PhoneDCDAO> getPhone() {
        return phone;
    }

    public void setPhone(List<PhoneDCDAO> phone) {
        this.phone = phone;
    }

    public List<MailboxDCDAO> getEmail() {
        return email;
    }

    public void setEmail(List<MailboxDCDAO> email) {
        this.email = email;
    }

    public List<WebDocumentDCDAO> getContactForm() {
        return contactForm;
    }

    public void setContactForm(List<WebDocumentDCDAO> contactForm) {
        this.contactForm = contactForm;
    }
}
