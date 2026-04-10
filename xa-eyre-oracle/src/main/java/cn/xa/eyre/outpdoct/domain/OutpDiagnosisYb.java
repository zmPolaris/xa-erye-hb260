package cn.xa.eyre.outpdoct.domain;

import java.util.Date;

public class OutpDiagnosisYb extends OutpDiagnosisYbKey {
    private String patientId;

    private String diagDesc;

    private Short pending;

    private String custom;

    private String diagClass;

    private Date diagnosisTime;

    private String postfix;

    private Short diagnosisNo;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId == null ? null : patientId.trim();
    }

    public String getDiagDesc() {
        return diagDesc;
    }

    public void setDiagDesc(String diagDesc) {
        this.diagDesc = diagDesc == null ? null : diagDesc.trim();
    }

    public Short getPending() {
        return pending;
    }

    public void setPending(Short pending) {
        this.pending = pending;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom == null ? null : custom.trim();
    }

    public String getDiagClass() {
        return diagClass;
    }

    public void setDiagClass(String diagClass) {
        this.diagClass = diagClass == null ? null : diagClass.trim();
    }

    public Date getDiagnosisTime() {
        return diagnosisTime;
    }

    public void setDiagnosisTime(Date diagnosisTime) {
        this.diagnosisTime = diagnosisTime;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix == null ? null : postfix.trim();
    }

    public Short getDiagnosisNo() {
        return diagnosisNo;
    }

    public void setDiagnosisNo(Short diagnosisNo) {
        this.diagnosisNo = diagnosisNo;
    }
}