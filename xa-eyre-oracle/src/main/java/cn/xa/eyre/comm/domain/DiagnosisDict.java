package cn.xa.eyre.comm.domain;

import java.util.Date;

public class DiagnosisDict extends DiagnosisDictKey {
    private Short stdIndicator;

    private Short approvedIndicator;

    private Date createDate;

    private String inputCode;

    public Short getStdIndicator() {
        return stdIndicator;
    }

    public void setStdIndicator(Short stdIndicator) {
        this.stdIndicator = stdIndicator;
    }

    public Short getApprovedIndicator() {
        return approvedIndicator;
    }

    public void setApprovedIndicator(Short approvedIndicator) {
        this.approvedIndicator = approvedIndicator;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getInputCode() {
        return inputCode;
    }

    public void setInputCode(String inputCode) {
        this.inputCode = inputCode == null ? null : inputCode.trim();
    }
}