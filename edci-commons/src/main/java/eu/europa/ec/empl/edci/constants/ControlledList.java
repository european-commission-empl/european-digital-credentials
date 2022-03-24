package eu.europa.ec.empl.edci.constants;

import eu.europa.ec.empl.edci.parsers.ControlledListParser;
import eu.europa.ec.empl.edci.parsers.rdf.RDFParser;
import eu.europa.ec.empl.edci.parsers.rdf.RDFParserTree;
import eu.europa.ec.empl.edci.parsers.rdf.RDFParserTreeLeaf;

import java.util.Arrays;

public enum ControlledList {

    //RDF sources
    HUMAN_SEX("http://publications.europa.eu/resource/authority/human-sex", RDFParser.class, "human-sex", false),
    LANGUAGE("http://publications.europa.eu/resource/authority/language", RDFParser.class, "language", false),
    COUNTRY("http://publications.europa.eu/resource/authority/country", RDFParser.class, "country", false),
    ATU("http://publications.europa.eu/resource/authority/atu", RDFParser.class, "atu", false),
    CURRENCY("http://publications.europa.eu/resource/authority/currency", RDFParser.class, "currency", false),
    FILE_TYPE("http://publications.europa.eu/resource/authority/file-type", RDFParser.class, "file-type", false),

    CORPORATE_BODY("http://publications.europa.eu/resource/authority/corporate-body", RDFParser.class, "corporate-body", false),
    DATASET_TYPE("http://publications.europa.eu/resource/authority/dataset-type", RDFParser.class, "dataset-type", false),
    FREQUENCY("http://publications.europa.eu/resource/authority/frequency", RDFParser.class, "frequency", false),
    NUTS("http://publications.europa.eu/resource/authority/nuts", RDFParser.class, "nuts", false), //TODO: found it by chance. Will we use it somewhere?

    CREDENTIAL_TYPE("http://data.europa.eu/snb/credential/25831c2", RDFParser.class, "credential", true),
    VERIFICATION_TYPE("http://data.europa.eu/snb/verifiable-presentation/25831c2", RDFParser.class, "verification", true),
    LEARNING_ACT("http://data.europa.eu/snb/learning-activity/25831c2", RDFParser.class, "learning-activity", false),
    LEARNING_SETTING("http://data.europa.eu/snb/learning-setting/25831c2", RDFParser.class, "learning-setting", true),
    ASSESSMENT("http://data.europa.eu/snb/assessment/25831c2", RDFParser.class, "assessment", false),
    ENCODING("http://data.europa.eu/snb/encoding/25831c2", RDFParser.class, "encoding", false),
    LEARNING_SCHECDULE("http://data.europa.eu/snb/learning-schedule/25831c2", RDFParser.class, "learning-schedule", false),
    LEARNING_OPPORTUNITY("http://data.europa.eu/snb/learning-opportunity/25831c2", RDFParser.class, "learning-opportunity", false),
    LEARNING_ASSESSMENT("http://data.europa.eu/snb/learning-assessment/25831c2", RDFParser.class, "learning-assessment", false),
    TARGET_GROUPS("http://data.europa.eu/snb/target-groups/25831c2", RDFParser.class, "target-groups", false),
    SUPERV_VERIF("http://data.europa.eu/snb/supervision-verification/25831c2", RDFParser.class, "supervision-verification", true),
    EDUCATION_CREDIT("http://data.europa.eu/snb/education-credit/25831c2", RDFParser.class, "education-credit", false),
    ACCREDITATION("http://data.europa.eu/snb/accreditation/25831c2", RDFParser.class, "accreditation", true),
    COM_CHANNEL("http://data.europa.eu/snb/com-channel/25831c2", RDFParser.class, "com-channel", false),
    COM_CHANNEL_USG("http://data.europa.eu/snb/com-channel-usg/25831c2", RDFParser.class, "com-channel-usg", false),
    VERIFICATION_CHECKS("http://data.europa.eu/snb/verification/25831c2", RDFParser.class, "verification-checks", false),
    VERIFICATION_STATUS("http://data.europa.eu/snb/verification-status/25831c2", RDFParser.class, "verification-status", true),
    ENTITLEMENT("http://data.europa.eu/snb/entitlement/25831c2", RDFParser.class, "entitlement", false),
    ENTITLEMENT_STATUS("http://data.europa.eu/snb/entitlement-status/25831c2", RDFParser.class, "entitlement-status", true),
    EQF("http://data.europa.eu/snb/eqf/25831c2", RDFParser.class, "eqf", true),
    NQF("http://data.europa.eu/snb/qdr/25831c2", RDFParserTreeLeaf.class, "qdr", true),

    ISCED_F("http://data.europa.eu/snb/isced-f/25831c2", RDFParserTree.class, "isced-f", true),
    QUALIFICATION_TOPIC("http://data.europa.eu/snb/qualification-topic/25831c2", RDFParser.class, "qualification-topic", false),
    SKILL_TYPE("http://data.europa.eu/snb/skill-type/25831c2", RDFParser.class, "skill-type", false),
    SKILL_REUSE_LEVEL("http://data.europa.eu/snb/skill-reuse-level/25831c2", RDFParser.class, "skill-reuse-level", false);

    private String url;
    @Deprecated
    private Class<? extends ControlledListParser> parser;
    private String name;
    private boolean europassCL;

    private ControlledList(String url, Class<? extends ControlledListParser> parser, String name, boolean europassCL) {
        this.url = url;
        this.parser = parser;
        this.name = name;
        this.europassCL = europassCL;
    }

    public static boolean contains(String uri) {
        return Arrays.stream(ControlledList.values()).anyMatch(item -> item.getUrl().equals(uri));
    }

    public static boolean isEuropassCl(String uri) {
        ControlledList cl = ControlledList.getByFrameworkUri(uri);
        return cl != null ? cl.isEuropassCL() : false;
    }

    public static ControlledList getByFrameworkUri(String uri) {
        return Arrays.stream(ControlledList.values()).filter(rdf -> rdf.getUrl().equalsIgnoreCase(uri)).findFirst().orElse(null);
    }

    public static ControlledList getByName(String name) {
        return Arrays.stream(ControlledList.values()).filter(rdf -> rdf.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public String getUrl() {
        return url;
    }

    @Deprecated
    public Class<? extends ControlledListParser> getParser() {
        return parser;
    }

    @Deprecated
    public void setParser(Class<? extends ControlledListParser> parser) {
        this.parser = parser;
    }

    public String getName() {
        return name;
    }


    public boolean isEuropassCL() {
        return europassCL;
    }

}
