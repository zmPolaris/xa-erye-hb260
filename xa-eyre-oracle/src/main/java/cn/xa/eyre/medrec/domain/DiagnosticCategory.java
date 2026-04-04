package cn.xa.eyre.medrec.domain;

public class DiagnosticCategory extends DiagnosticCategoryKey {
    private Short codeNo;

    private String diagnosisCodeGl;

    public Short getCodeNo() {
        return codeNo;
    }

    public void setCodeNo(Short codeNo) {
        this.codeNo = codeNo;
    }

    public String getDiagnosisCodeGl() {
        return diagnosisCodeGl;
    }

    public void setDiagnosisCodeGl(String diagnosisCodeGl) {
        this.diagnosisCodeGl = diagnosisCodeGl == null ? null : diagnosisCodeGl.trim();
    }
}