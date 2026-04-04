package cn.xa.eyre.lab.domain;

import java.util.Date;

public class LabResult extends LabResultKey {
    private String reportItemName;

    private String reportItemCode;

    private String result;

    private String units;

    private String abnormalIndicator;

    private String instrumentId;

    private Date resultDateTime;

    private String resultRange;

    private String printContext;

    private String referContext;

    private String sampleid;

    public String getReportItemName() {
        return reportItemName;
    }

    public void setReportItemName(String reportItemName) {
        this.reportItemName = reportItemName == null ? null : reportItemName.trim();
    }

    public String getReportItemCode() {
        return reportItemCode;
    }

    public void setReportItemCode(String reportItemCode) {
        this.reportItemCode = reportItemCode == null ? null : reportItemCode.trim();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units == null ? null : units.trim();
    }

    public String getAbnormalIndicator() {
        return abnormalIndicator;
    }

    public void setAbnormalIndicator(String abnormalIndicator) {
        this.abnormalIndicator = abnormalIndicator == null ? null : abnormalIndicator.trim();
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId == null ? null : instrumentId.trim();
    }

    public Date getResultDateTime() {
        return resultDateTime;
    }

    public void setResultDateTime(Date resultDateTime) {
        this.resultDateTime = resultDateTime;
    }

    public String getResultRange() {
        return resultRange;
    }

    public void setResultRange(String resultRange) {
        this.resultRange = resultRange == null ? null : resultRange.trim();
    }

    public String getPrintContext() {
        return printContext;
    }

    public void setPrintContext(String printContext) {
        this.printContext = printContext == null ? null : printContext.trim();
    }

    public String getReferContext() {
        return referContext;
    }

    public void setReferContext(String referContext) {
        this.referContext = referContext == null ? null : referContext.trim();
    }

    public String getSampleid() {
        return sampleid;
    }

    public void setSampleid(String sampleid) {
        this.sampleid = sampleid == null ? null : sampleid.trim();
    }
}