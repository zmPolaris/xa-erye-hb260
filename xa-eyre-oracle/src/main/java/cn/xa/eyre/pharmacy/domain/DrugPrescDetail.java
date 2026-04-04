package cn.xa.eyre.pharmacy.domain;

import java.math.BigDecimal;
import java.util.Date;

public class DrugPrescDetail {
    private Date prescDate;

    private Short prescNo;

    private Short itemNo;

    private String drugCode;

    private String drugSpec;

    private String drugName;

    private String firmId;

    private String packageSpec;

    private String packageUnits;

    private Short quantity;

    private BigDecimal costs;

    private BigDecimal payments;



    public Date getPrescDate() {
        return prescDate;
    }

    public void setPrescDate(Date prescDate) {
        this.prescDate = prescDate;
    }

    public Short getPrescNo() {
        return prescNo;
    }

    public void setPrescNo(Short prescNo) {
        this.prescNo = prescNo;
    }

    public Short getItemNo() {
        return itemNo;
    }

    public void setItemNo(Short itemNo) {
        this.itemNo = itemNo;
    }

    public String getDrugCode() {
        return drugCode;
    }

    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode == null ? null : drugCode.trim();
    }

    public String getDrugSpec() {
        return drugSpec;
    }

    public void setDrugSpec(String drugSpec) {
        this.drugSpec = drugSpec == null ? null : drugSpec.trim();
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName == null ? null : drugName.trim();
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId == null ? null : firmId.trim();
    }

    public String getPackageSpec() {
        return packageSpec;
    }

    public void setPackageSpec(String packageSpec) {
        this.packageSpec = packageSpec == null ? null : packageSpec.trim();
    }

    public String getPackageUnits() {
        return packageUnits;
    }

    public void setPackageUnits(String packageUnits) {
        this.packageUnits = packageUnits == null ? null : packageUnits.trim();
    }

    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCosts() {
        return costs;
    }

    public void setCosts(BigDecimal costs) {
        this.costs = costs;
    }

    public BigDecimal getPayments() {
        return payments;
    }

    public void setPayments(BigDecimal payments) {
        this.payments = payments;
    }


}