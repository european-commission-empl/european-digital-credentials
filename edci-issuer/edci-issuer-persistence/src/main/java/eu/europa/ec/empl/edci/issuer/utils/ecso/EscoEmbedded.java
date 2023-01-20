package eu.europa.ec.empl.edci.issuer.utils.ecso;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EscoEmbedded<E> {

    @JsonProperty("results")
    List<E> results;

    public List<E> getResults() {
        return results;
    }

    public void setResults(List<E> results) {
        this.results = results;
    }
}