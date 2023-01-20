package eu.europa.ec.empl.edci.issuer.web.model;

import eu.europa.ec.empl.edci.issuer.web.model.data.RecipientDataView;

import java.util.List;

public class RecipientFileUploadResponseView {

    private boolean valid;
    private List<RecipientDataView> recipientDataViews;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<RecipientDataView> getRecipientDataViews() {
        return recipientDataViews;
    }

    public void setRecipientDataViews(List<RecipientDataView> recipientDataViews) {
        this.recipientDataViews = recipientDataViews;
    }
}
