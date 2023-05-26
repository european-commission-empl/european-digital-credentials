package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:note:")
@CustomizableEntityDTO(identifierField = "subject")
public class QDRNoteDTO extends QDRJsonLdCommonDTO {

    private QDRConceptDTO noteFormat;
    @NotNull
    private String noteLiteral;
    private QDRConceptDTO subject;

    public String getNoteLiteral() {
        return noteLiteral;
    }

    public void setNoteLiteral(String noteLiteral) {
        this.noteLiteral = noteLiteral;
    }

    public QDRConceptDTO getSubject() {
        return subject;
    }

    public void setSubject(QDRConceptDTO subject) {
        this.subject = subject;
    }

    public QDRConceptDTO getNoteFormat() {
        return noteFormat;
    }

    public void setNoteFormat(QDRConceptDTO noteFormat) {
        this.noteFormat = noteFormat;
    }

    @Override
    public String toString() {
        return noteLiteral.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRNoteDTO)) return false;
        if (!super.equals(o)) return false;
        QDRNoteDTO noteDTO = (QDRNoteDTO) o;
        return Objects.equals(noteFormat, noteDTO.noteFormat) &&
                Objects.equals(noteLiteral, noteDTO.noteLiteral) &&
                Objects.equals(subject, noteDTO.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), noteFormat, noteLiteral, subject);
    }
}
