package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.ITranslatable;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:note:")
@CustomizableEntityDTO(identifierField = "subject")
public class NoteDTO extends JsonLdCommonDTO implements ITranslatable {

    private ConceptDTO noteFormat;
    @NotNull
    private LiteralMap noteLiteral;
    private ConceptDTO subject;

    public NoteDTO() {
        super();
    }

    @JsonCreator
    public NoteDTO(String uri) {
        super(uri);
    }

    @Override
    public LiteralMap getContents() {
        return noteLiteral;
    }

    public LiteralMap getNoteLiteral() {
        return noteLiteral;
    }

    public void setNoteLiteral(LiteralMap noteLiteral) {
        this.noteLiteral = noteLiteral;
    }

    public ConceptDTO getSubject() {
        return subject;
    }

    public void setSubject(ConceptDTO subject) {
        this.subject = subject;
    }

    public ConceptDTO getNoteFormat() {
        return noteFormat;
    }

    public void setNoteFormat(ConceptDTO noteFormat) {
        this.noteFormat = noteFormat;
    }

    @Override
    public String toString() {
        return noteLiteral.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteDTO)) return false;
        if (!super.equals(o)) return false;
        NoteDTO noteDTO = (NoteDTO) o;
        return Objects.equals(noteFormat, noteDTO.noteFormat) &&
                Objects.equals(noteLiteral, noteDTO.noteLiteral) &&
                Objects.equals(subject, noteDTO.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), noteFormat, noteLiteral, subject);
    }
}
