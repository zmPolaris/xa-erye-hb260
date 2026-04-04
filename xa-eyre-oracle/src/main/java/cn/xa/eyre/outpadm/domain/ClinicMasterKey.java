package cn.xa.eyre.outpadm.domain;

import java.util.Date;

public class ClinicMasterKey {
    private Date visitDate;

    private Short visitNo;

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Short getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(Short visitNo) {
        this.visitNo = visitNo;
    }
}