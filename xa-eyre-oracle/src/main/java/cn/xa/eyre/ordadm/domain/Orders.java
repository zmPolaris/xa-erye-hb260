package cn.xa.eyre.ordadm.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Orders extends OrdersKey {
    private Short repeatIndicator;

    private String orderClass;

    private String orderText;

    private String orderCode;

    private BigDecimal dosage;

    private String dosageUnits;

    private String administration;

    private Short duration;

    private String durationUnits;

    private Date startDateTime;

    private Date stopDateTime;

    private String frequency;

    private Short freqCounter;

    private Short freqInterval;

    private String freqIntervalUnit;

    private String freqDetail;

    private String performSchedule;

    private String performResult;

    private String orderingDept;

    private String doctor;

    private String stopDoctor;

    private String nurse;

    private Date enterDateTime;

    private String orderStatus;

    private Short billingAttr;

    private Date lastPerformDateTime;

    private Date lastAcctingDateTime;

    private String stopNurse;

    private Date stopOrderDateTime;

    private Short drugBillingAttr;

    private String printFlag;

    private String printFlag1;

    private String printFlag3;

    private String relativeNo;

    private String orderTypeName;

    private String orderPerformStatus;

    private Short queryOrderNo;

    private Short queryOrderSubNo;

    private String fallbackOrder;

    public Short getRepeatIndicator() {
        return repeatIndicator;
    }

    public void setRepeatIndicator(Short repeatIndicator) {
        this.repeatIndicator = repeatIndicator;
    }

    public String getOrderClass() {
        return orderClass;
    }

    public void setOrderClass(String orderClass) {
        this.orderClass = orderClass == null ? null : orderClass.trim();
    }

    public String getOrderText() {
        return orderText;
    }

    public void setOrderText(String orderText) {
        this.orderText = orderText == null ? null : orderText.trim();
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public BigDecimal getDosage() {
        return dosage;
    }

    public void setDosage(BigDecimal dosage) {
        this.dosage = dosage;
    }

    public String getDosageUnits() {
        return dosageUnits;
    }

    public void setDosageUnits(String dosageUnits) {
        this.dosageUnits = dosageUnits == null ? null : dosageUnits.trim();
    }

    public String getAdministration() {
        return administration;
    }

    public void setAdministration(String administration) {
        this.administration = administration == null ? null : administration.trim();
    }

    public Short getDuration() {
        return duration;
    }

    public void setDuration(Short duration) {
        this.duration = duration;
    }

    public String getDurationUnits() {
        return durationUnits;
    }

    public void setDurationUnits(String durationUnits) {
        this.durationUnits = durationUnits == null ? null : durationUnits.trim();
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getStopDateTime() {
        return stopDateTime;
    }

    public void setStopDateTime(Date stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency == null ? null : frequency.trim();
    }

    public Short getFreqCounter() {
        return freqCounter;
    }

    public void setFreqCounter(Short freqCounter) {
        this.freqCounter = freqCounter;
    }

    public Short getFreqInterval() {
        return freqInterval;
    }

    public void setFreqInterval(Short freqInterval) {
        this.freqInterval = freqInterval;
    }

    public String getFreqIntervalUnit() {
        return freqIntervalUnit;
    }

    public void setFreqIntervalUnit(String freqIntervalUnit) {
        this.freqIntervalUnit = freqIntervalUnit == null ? null : freqIntervalUnit.trim();
    }

    public String getFreqDetail() {
        return freqDetail;
    }

    public void setFreqDetail(String freqDetail) {
        this.freqDetail = freqDetail == null ? null : freqDetail.trim();
    }

    public String getPerformSchedule() {
        return performSchedule;
    }

    public void setPerformSchedule(String performSchedule) {
        this.performSchedule = performSchedule == null ? null : performSchedule.trim();
    }

    public String getPerformResult() {
        return performResult;
    }

    public void setPerformResult(String performResult) {
        this.performResult = performResult == null ? null : performResult.trim();
    }

    public String getOrderingDept() {
        return orderingDept;
    }

    public void setOrderingDept(String orderingDept) {
        this.orderingDept = orderingDept == null ? null : orderingDept.trim();
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor == null ? null : doctor.trim();
    }

    public String getStopDoctor() {
        return stopDoctor;
    }

    public void setStopDoctor(String stopDoctor) {
        this.stopDoctor = stopDoctor == null ? null : stopDoctor.trim();
    }

    public String getNurse() {
        return nurse;
    }

    public void setNurse(String nurse) {
        this.nurse = nurse == null ? null : nurse.trim();
    }

    public Date getEnterDateTime() {
        return enterDateTime;
    }

    public void setEnterDateTime(Date enterDateTime) {
        this.enterDateTime = enterDateTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

    public Short getBillingAttr() {
        return billingAttr;
    }

    public void setBillingAttr(Short billingAttr) {
        this.billingAttr = billingAttr;
    }

    public Date getLastPerformDateTime() {
        return lastPerformDateTime;
    }

    public void setLastPerformDateTime(Date lastPerformDateTime) {
        this.lastPerformDateTime = lastPerformDateTime;
    }

    public Date getLastAcctingDateTime() {
        return lastAcctingDateTime;
    }

    public void setLastAcctingDateTime(Date lastAcctingDateTime) {
        this.lastAcctingDateTime = lastAcctingDateTime;
    }

    public String getStopNurse() {
        return stopNurse;
    }

    public void setStopNurse(String stopNurse) {
        this.stopNurse = stopNurse == null ? null : stopNurse.trim();
    }

    public Date getStopOrderDateTime() {
        return stopOrderDateTime;
    }

    public void setStopOrderDateTime(Date stopOrderDateTime) {
        this.stopOrderDateTime = stopOrderDateTime;
    }

    public Short getDrugBillingAttr() {
        return drugBillingAttr;
    }

    public void setDrugBillingAttr(Short drugBillingAttr) {
        this.drugBillingAttr = drugBillingAttr;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag == null ? null : printFlag.trim();
    }

    public String getPrintFlag1() {
        return printFlag1;
    }

    public void setPrintFlag1(String printFlag1) {
        this.printFlag1 = printFlag1 == null ? null : printFlag1.trim();
    }

    public String getPrintFlag3() {
        return printFlag3;
    }

    public void setPrintFlag3(String printFlag3) {
        this.printFlag3 = printFlag3 == null ? null : printFlag3.trim();
    }

    public String getRelativeNo() {
        return relativeNo;
    }

    public void setRelativeNo(String relativeNo) {
        this.relativeNo = relativeNo == null ? null : relativeNo.trim();
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName == null ? null : orderTypeName.trim();
    }

    public String getOrderPerformStatus() {
        return orderPerformStatus;
    }

    public void setOrderPerformStatus(String orderPerformStatus) {
        this.orderPerformStatus = orderPerformStatus == null ? null : orderPerformStatus.trim();
    }

    public Short getQueryOrderNo() {
        return queryOrderNo;
    }

    public void setQueryOrderNo(Short queryOrderNo) {
        this.queryOrderNo = queryOrderNo;
    }

    public Short getQueryOrderSubNo() {
        return queryOrderSubNo;
    }

    public void setQueryOrderSubNo(Short queryOrderSubNo) {
        this.queryOrderSubNo = queryOrderSubNo;
    }

    public String getFallbackOrder() {
        return fallbackOrder;
    }

    public void setFallbackOrder(String fallbackOrder) {
        this.fallbackOrder = fallbackOrder == null ? null : fallbackOrder.trim();
    }
}