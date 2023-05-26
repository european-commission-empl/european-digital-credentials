package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.AgentDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.ContactPointDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.LocationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity(name = GroupDCDAO.TABLE)
@Table(name = GroupDCDAO.TABLE)
//@CustomizableEntity(identifierField = "location", entityCode = "LOC")
public class GroupDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_GROUP";
    public static final String TABLE_SHORT = "DC_GROUP";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "PREF_LABEL_PK", referencedColumnName = "PK", nullable = false)
    private TextDTDAO prefLabel; //1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CONT_POI",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ContactPointDCDAO.TABLE_PK_REF))
    private Set<ContactPointDCDAO> contactPoint; //*

    public GroupDCDAO() {

    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public TextDTDAO getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(TextDTDAO prefLabel) {
        this.prefLabel = prefLabel;
    }

    public Set<ContactPointDCDAO> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(Set<ContactPointDCDAO> contactPoint) {
        this.contactPoint = contactPoint;
    }
}