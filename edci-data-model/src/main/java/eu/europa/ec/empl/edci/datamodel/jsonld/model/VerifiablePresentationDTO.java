package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VerifiablePresentationDTO extends JsonLdCommonDTO {
    private List<URI> proof = new ArrayList<>();
    private List<VerifiableCredentialDTO> verifiableCredential = new ArrayList<>();
    private List<AgentDTO> holder = new ArrayList<>();

    public List<URI> getProof() {
        return proof;
    }

    public List<VerifiableCredentialDTO> getVerifiableCredential() {
        return verifiableCredential;
    }

    public List<AgentDTO> getHolder() {
        return holder;
    }

    public void setProof(List<URI> proof) {
        this.proof = proof;
    }

    public void setVerifiableCredential(List<VerifiableCredentialDTO> verifiableCredential) {
        this.verifiableCredential = verifiableCredential;
    }

    public void setHolder(List<AgentDTO> holder) {
        this.holder = holder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerifiablePresentationDTO)) return false;
        if (!super.equals(o)) return false;
        VerifiablePresentationDTO that = (VerifiablePresentationDTO) o;
        return Objects.equals(proof, that.proof) &&
                Objects.equals(verifiableCredential, that.verifiableCredential) &&
                Objects.equals(holder, that.holder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), proof, verifiableCredential, holder);
    }
}
