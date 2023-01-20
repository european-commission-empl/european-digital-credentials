package eu.europa.ec.empl.edci.wallet.common.model.verification;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "qmsAccreditations")
@XmlAccessorType(XmlAccessType.FIELD)

public class QMSAccreditations {
    @XmlElements({
            @XmlElement(name = "qmsaccreditation", type = InstitutionalAccreditation.class),
            @XmlElement(name = "qmsaccreditation", type = QualificationAccreditation.class),
    })
    List<QMSAccreditation> accreditation = new ArrayList<QMSAccreditation>();

    public List<QMSAccreditation> getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(List<QMSAccreditation> accreditation) {
        this.accreditation = accreditation;
    }
}
