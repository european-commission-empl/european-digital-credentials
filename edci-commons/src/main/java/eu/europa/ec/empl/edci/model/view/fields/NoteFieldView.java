package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class NoteFieldView {

    private String noteFormat;
    private List<String> noteLiteral;
    private String subject;

    public String getNoteFormat() {
        return noteFormat;
    }

    public void setNoteFormat(String noteFormat) {
        this.noteFormat = noteFormat;
    }

    public List<String> getNoteLiteral() {
        return noteLiteral;
    }

    public void setNoteLiteral(List<String> noteLiteral) {
        this.noteLiteral = noteLiteral;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
