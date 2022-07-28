package eu.europa.ec.empl.edci.issuer.utils.ecso;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EscoSearchResults {

    @JsonProperty("_embedded")
    EscoEmbedded<EscoElementPayload> embedded;

    @JsonProperty("total")
    public Long total;

    @JsonProperty("offset")
    public Long offset;

    @JsonProperty("limit")
    public Long limit;

    public EscoEmbedded<EscoElementPayload> getEmbedded() {
        return embedded;
    }

    public void setEmbedded(EscoEmbedded<EscoElementPayload> embedded) {
        this.embedded = embedded;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }
}

