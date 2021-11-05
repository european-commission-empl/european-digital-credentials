package eu.europa.ec.empl.edci.issuer.common.constants;

import eu.europa.ec.empl.edci.constants.EDCIEndpoint;

public class IssuerEndpoint extends EDCIEndpoint {

    public class V1 {

        public static final String FILES_BASE = "/files";
        public static final String CREDENTIALS_BASE = "/credentials";
        public static final String USER_BASE = "/users";
        public static final String PUBLIC = "/public";

        public static final String ROOT = "";

        public static final String TEMPLATES = "/templates";
        public static final String CREDENTIALS = "/credentials";
        public static final String RECIPIENTS = "/recipients";
        public static final String SPECS = "/specs";
        public static final String LOGO = "/logo";
        public static final String BACKGROUND = "/background";

        public static final String GENERIC_ENTITY = "/entity";

        public static final String CREDENTIALS_ISSUE = "/issue";
        public static final String CREDENTIALS_UPLOAD = "/upload";
        public static final String CREDENTIALS_SEAL = "/seal";
        public static final String CREDENTIALS_SEAL_SEND = "/seal_and_send";
        public static final String CREDENTIALS_SEAL_LOCAL = "/seal_local";
        public static final String CREDENTIALS_SEAL_BATCH = "/seal_batch";
        public static final String CREDENTIALS_SEND = "/send";
        public static final String CREDENTIALS_BYTES = "/signature-bytes";


        public static final String ORGANIZATIONS_BASE = "/organizations";
        public static final String ACHIEVEMENTS_BASE = "/achievements";
        public static final String ASSESSMENTS_BASE = "/assessments";
        public static final String ACTIVITIES_BASE = "/activities";
        public static final String LEARNING_OUTCOMES_BASE = "/learningOutcomes";
        public static final String ENTITLEMENTS_BASE = "/entitlements";
        public static final String DIPLOMA_BASE = "/diploma";

        public static final String CRED_ACHIEVED = "/achieved";
        public static final String CRED_PERFORMED = "/performed";
        public static final String CRED_ENTITLED_TO = "/entitledTo";
        public static final String CRED_ISSUER = "/issuer";
        public static final String CRED_ISSUE_ASSESSMENTS = "/issueAssessments";
        public static final String CRED_DIPLOMA = "/diploma";

        public static final String ORG_HAS_UNITS_REL = "/hasUnits";
        public static final String ORG_UNIT_OF_REL = "/unitOf";

        public static final String ENT_HAS_PART = "/hasPart";
        public static final String ENT_VALID_WITH = "/validWith";

        public static final String ASS_HAS_PART = "/hasPart";
        public static final String ASS_ASSESSED_BY = "/assessedBy";

        public static final String ACT_DIRECTED_BY = "/directedBy";
        public static final String ACT_INFLUENCED = "/influenced";
        public static final String ACT_HAS_PART = "/hasPart";

        public static final String ACH_AWARDING_BODY = "/awardingBody";
        public static final String ACH_PROVEN_BY = "/provenBy";
        public static final String ACH_INFLUENCED_BY = "/influencedBy";
        public static final String ACH_ENTITLES_TO = "/entitlesTo";
        public static final String ACH_SUB_ACHIEVEMENTS = "/subAchievements";
        public static final String ACH_LEARNING_OUTCOMES = "/learningOutcomes";

        public static final String USER_DETAILS = "/details";
        public static final String GENERIC_ENTITIES_BASE = "/data";

        public static final String TEST_CREDENTIAL = "/testCredential";

    }


}
