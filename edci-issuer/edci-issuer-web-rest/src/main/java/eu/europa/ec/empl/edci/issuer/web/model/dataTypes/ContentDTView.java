package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import javax.validation.constraints.Size;
import javax.ws.rs.core.MediaType;

public class ContentDTView extends DataTypeView {

    public ContentDTView() {
    }

    public ContentDTView(String content, String language) {
        this.content = content;
        this.language = language;
    }

    @Size(max = 4000)
    private String content; //1

    private String language; //0..1

    private String format = MediaType.TEXT_PLAIN; //0..1

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
