package eu.europa.ec.empl.edci.issuer.common.model;

import java.util.HashMap;
import java.util.Map;

public class AssessmentsListIssueDTO {


    private Map<Long, String> assessments = new HashMap<>();

    public Map<Long, String> getAssessments() {
        return assessments;
    }

    public void setAssessments(Map<Long, String> assessments) {
        this.assessments = assessments;
    }
}