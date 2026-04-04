package cn.xa.eyre.medrec.domain;

import java.util.Date;

public class Diagnosis extends DiagnosisKey {
    private String diagnosisDesc;

    private Date diagnosisDate;

    private Short treatDays;

    private String treatResult;

    private Short operTreatIndicator;

    private String codeVersion;

    private String admCondType;

    private String diagnosisDescGl;

    public String getDiagnosisDesc() {
        return diagnosisDesc;
    }

    public void setDiagnosisDesc(String diagnosisDesc) {
        this.diagnosisDesc = diagnosisDesc == null ? null : diagnosisDesc.trim();
    }

    public Date getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(Date diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public Short getTreatDays() {
        return treatDays;
    }

    public void setTreatDays(Short treatDays) {
        this.treatDays = treatDays;
    }

    public String getTreatResult() {
        return treatResult;
    }

    public void setTreatResult(String treatResult) {
        this.treatResult = treatResult == null ? null : treatResult.trim();
    }

    public Short getOperTreatIndicator() {
        return operTreatIndicator;
    }

    public void setOperTreatIndicator(Short operTreatIndicator) {
        this.operTreatIndicator = operTreatIndicator;
    }

    public String getCodeVersion() {
        return codeVersion;
    }

    public void setCodeVersion(String codeVersion) {
        this.codeVersion = codeVersion == null ? null : codeVersion.trim();
    }

    public String getAdmCondType() {
        return admCondType;
    }

    public void setAdmCondType(String admCondType) {
        this.admCondType = admCondType == null ? null : admCondType.trim();
    }

    public String getDiagnosisDescGl() {
        return diagnosisDescGl;
    }

    public void setDiagnosisDescGl(String diagnosisDescGl) {
        this.diagnosisDescGl = diagnosisDescGl == null ? null : diagnosisDescGl.trim();
    }
}