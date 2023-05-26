package eu.europa.ec.empl.edci.issuer.web.model;

import java.util.ArrayList;
import java.util.List;

public class SubresourcesOids {

    public List<Long> oid = new ArrayList<>();

    public List<Long> getOid() {
        return oid;
    }

    public Long getSingleOid() {
        return oid != null && !oid.isEmpty() ? oid.get(0) : null;
    }

    public void setOid(List<Long> oid) {
        this.oid = oid;
    }

}