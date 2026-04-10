package cn.xa.eyre.outpdoct.mapper;

import cn.xa.eyre.outpdoct.domain.OutpDiagnosisYb;
import cn.xa.eyre.outpdoct.domain.OutpDiagnosisYbKey;

import java.util.List;

public interface OutpDiagnosisYbMapper {
    int deleteByPrimaryKey(OutpDiagnosisYbKey key);

    int insert(OutpDiagnosisYb record);

    int insertSelective(OutpDiagnosisYb record);

    OutpDiagnosisYb selectByPrimaryKey(OutpDiagnosisYbKey key);

    int updateByPrimaryKeySelective(OutpDiagnosisYb record);

    int updateByPrimaryKey(OutpDiagnosisYb record);

    List<OutpDiagnosisYb> selectOutpDiagYbByCondition(OutpDiagnosisYb outpDiagnosisYbVo);
}