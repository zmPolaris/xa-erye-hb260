package cn.xa.eyre.outpadm.mapper;

import cn.xa.eyre.outpadm.domain.ClinicMaster;
import cn.xa.eyre.outpadm.domain.ClinicMasterKey;

public interface ClinicMasterMapper {
    int deleteByPrimaryKey(ClinicMasterKey key);

    int insert(ClinicMaster record);

    int insertSelective(ClinicMaster record);

    ClinicMaster selectByPrimaryKey(ClinicMasterKey key);

    int updateByPrimaryKeySelective(ClinicMaster record);

    int updateByPrimaryKey(ClinicMaster record);
}