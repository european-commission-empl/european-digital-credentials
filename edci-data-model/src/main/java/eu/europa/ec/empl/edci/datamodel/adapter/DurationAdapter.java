package eu.europa.ec.empl.edci.datamodel.adapter;

import org.joda.time.Period;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;

public class DurationAdapter extends XmlAdapter<javax.xml.datatype.Duration, Period> {

    @Override
    public Period unmarshal(javax.xml.datatype.Duration v) throws Exception {
        return Period.parse(v.toString());
    }

    @Override
    public javax.xml.datatype.Duration marshal(Period v) throws Exception {
        return DatatypeFactory.newInstance().newDuration(v.toString());
    }
}
