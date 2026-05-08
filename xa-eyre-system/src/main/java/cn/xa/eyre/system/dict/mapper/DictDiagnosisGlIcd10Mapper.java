package cn.xa.eyre.system.dict.mapper;

import cn.xa.eyre.system.dict.domain.DictDiagnosisGlIcd10;
import org.apache.ibatis.annotations.Param;

public interface DictDiagnosisGlIcd10Mapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DictDiagnosisGlIcd10 record);

    int insertSelective(DictDiagnosisGlIcd10 record);

    DictDiagnosisGlIcd10 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DictDiagnosisGlIcd10 record);

    int updateByPrimaryKey(DictDiagnosisGlIcd10 record);

    /**
     * @description 门诊疾病对应编码
     **/
    DictDiagnosisGlIcd10 selectByEmrCode(@Param("emrCode") String emrCode);

}