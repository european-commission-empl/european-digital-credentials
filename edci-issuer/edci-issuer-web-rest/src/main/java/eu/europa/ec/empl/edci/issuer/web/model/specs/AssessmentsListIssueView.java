package eu.europa.ec.empl.edci.issuer.web.model.specs;

import java.util.HashMap;
import java.util.Map;

public class AssessmentsListIssueView {


    private Map<Long, String> assessments = new HashMap<>();

    public Map<Long, String> getAssessments() {
        return assessments;
    }

    public void setAssessments(Map<Long, String> assessments) {
        this.assessments = assessments;
    }
}