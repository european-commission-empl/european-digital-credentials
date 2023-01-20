package eu.europa.ec.empl.edci.datamodel.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CDATAAdapter extends XmlAdapter<String, String> {

    @Override
    public String marshal(String string) throws Exception {
        if (string.contains("&") || string.contains("<") || string.contains("\"")) {
            return "<![CDATA[" + string + "]]>";
        } else {
            return string;
        }
    }

    @Override
    public String unmarshal(String string) throws Exception {
        return string;
    }
}
