package cn.xa.eyre.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.xa.eyre.comm.domain.Users;
import cn.xa.eyre.common.constant.Constants;
import cn.xa.eyre.common.core.domain.R;
import cn.xa.eyre.common.core.kafka.DBMessage;
import cn.xa.eyre.common.utils.DateUtils;
import cn.xa.eyre.common.utils.FuzzyMatcher;
import cn.xa.eyre.common.utils.StringUtils;
import cn.xa.eyre.hisapi.*;
import cn.xa.eyre.hub.domain.emrmonitor.*;
import cn.xa.eyre.hub.domain.emrreal.EmrActivityInfo;
import cn.xa.eyre.hub.domain.emrreal.EmrPatientInfo;
import cn.xa.eyre.hub.service.SynchroBaseService;
import cn.xa.eyre.hub.service.SynchroEmrMonitorService;
import cn.xa.eyre.hub.service.SynchroEmrRealService;
import cn.xa.eyre.hub.staticvalue.HubCodeEnum;
import cn.xa.eyre.inpadm.domain.PatsInHospital;
import cn.xa.eyre.lab.domain.LabResult;
import cn.xa.eyre.lab.domain.LabTestMaster;
import cn.xa.eyre.medrec.domain.*;
import cn.xa.eyre.outpadm.domain.ClinicMaster;
import cn.xa.eyre.outpdoct.domain.OutpDiagnosisYb;
import cn.xa.eyre.outpdoct.domain.OutpMr;
import cn.xa.eyre.system.dict.domain.*;
import cn.xa.eyre.system.dict.mapper.*;
import cn.xa.eyre.system.temp.domain.DictTemp;
import cn.xa.eyre.system.temp.domain.HisDeptDict;
import cn.xa.eyre.system.temp.mapper.DictTempMapper;
import cn.xa.eyre.system.temp.mapper.HisDeptDictMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataConvertService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HisDeptDictMapper hisDeptDictMapper;// oracle数据
    @Autowired
    private DictTempMapper dictTempMapper;// 前置软件数据
    @Autowired
    private DictDisDeptMapper dictDisDeptMapper;// 转码表
    @Autowired
    private DictDiseaseIcd10Mapper dictDiseaseIcd10Mapper;// 转码表
    @Autowired
    private DdDiseaseIcdMapper ddDiseaseIcdMapper;// 前置软件诊断代码表
    @Autowired
    private DictSpecimenCategoryMapper dictSpecimenCategoryMapper;
    @Autowired
    private OutpdoctFeignClient outpdoctFeignClient;
    @Autowired
    private MedrecFeignClient medrecFeignClient;
    @Autowired
    private OutpadmFeignClient outpadmFeignClient;
    @Autowired
    private DdNationMapper ddNationMapper;
    @Autowired
    SynchroEmrRealService synchroEmrRealService;
    @Autowired
    private CommFeignClient commFeignClient;
    @Autowired
    private HubToolService hubToolService;
    @Autowired
    private InpadmFeignClient inpadmFeignClient;
    @Autowired
    private DictTreatResultMapper dictTreatResultMapper;

    @Resource
    SynchroBaseService synchroBaseService;

    @Autowired
    private SynchroEmrMonitorService synchroEmrMonitorService;
    @Autowired
    private LabFeignClient labFeignClient;
    @Autowired
    private DdExQuantificationMapper ddExQuantificationMapper;// 检验结果

    public boolean convertDept() {
        List<HisDeptDict> merList = hisDeptDictMapper.selectAll();

        List<DictTemp> hubList = dictTempMapper.selectAll();

        String hisName = null;
        DictDisDept dictDisDept = new DictDisDept();
        dictDisDept.setStatus(0);
        dictDisDept.setIsDefault(1);
        dictDisDept.setCreateTime(DateUtils.getNowDate());
        for (HisDeptDict his:merList) {
            boolean exist = false;
            hisName = his.getDeptName();
            // 精准匹配
            for (DictTemp temp: hubList) {
                if(temp.getName().equals(hisName)){
                    exist = true;
                    dictDisDept.setRemark("精准匹配");
                    dictDisDept.setEmrCode(his.getDeptCode());
                    dictDisDept.setEmrName(his.getDeptName());
                    dictDisDept.setHubCode(temp.getCode());
                    dictDisDept.setHubName(temp.getName());
                    break;
                }
            }
            // 模糊匹配
            if (!exist){
                // 模糊匹配
                for (DictTemp temp: hubList) {
                    if (FuzzyMatcher.fuzzyMatch(temp.getName(), hisName)) {
                        exist = true;
                        dictDisDept.setRemark("模糊匹配");
                        dictDisDept.setEmrCode(his.getDeptCode());
                        dictDisDept.setEmrName(his.getDeptName());
                        dictDisDept.setHubCode(temp.getCode());
                        dictDisDept.setHubName(temp.getName());
                        break;
                    }
                }
            }

            // 没找到
            if(!exist){
                dictDisDept.setRemark("在前置软件中没有同名的");
                dictDisDept.setEmrCode(his.getDeptCode());
                dictDisDept.setEmrName(his.getDeptName());
                dictDisDept.setHubCode("D99");
                dictDisDept.setHubName("其他科室");
            }
            dictDisDeptMapper.insertSelective(dictDisDept);
        }

        /*// 把HIS中没有的写入
        List<DictDisDept> list = dictDisDeptMapper.selectAll();
        for (DictTemp temp: hubList) {
            boolean exist = false;
            for (DictDisDept dept:list) {
                if(dept.getHubCode().equals(temp.getCode())){
                    exist = true;
                    break;// 优先匹配第一个
                }
            }
            if (!exist){
                dictDisDept.setRemark("在HIS中没有");
                dictDisDept.setHubCode(temp.getCode());
                dictDisDept.setHubName(temp.getName());
                dictDisDeptMapper.insertSelective(dictDisDept);
            }
        }*/

        return true;
    }

    public void baseDept(DBMessage dbMessage) {
        logger.debug("医院信息系统科室信息接口");
      //  synchroBaseService.syncBaseDept(dbMessage.getAfterData(), "add");
    }

    public void baseUser(DBMessage dbMessage) {
        logger.debug("医院信息系统用户信息接口");

    }

    public boolean convertDiseaseIcd() {
        /*List<DdDiseaseIcd> hubIcds = ddDiseaseIcdMapper.selectAll();
        R<List<GysybIcd10>> icd10ListResult = insuranceFeignClient.getGysybIcd10List();
        if (R.SUCCESS == icd10ListResult.getCode() && !icd10ListResult.getData().isEmpty()){
            for (GysybIcd10 emricd : icd10ListResult.getData()) {
                DictDiseaseIcd10 dictDiseaseIcd10 = new DictDiseaseIcd10();
                dictDiseaseIcd10.setEmrCode(emricd.getIcdCode());
                dictDiseaseIcd10.setEmrName(emricd.getIcdName());
                boolean match = false;
                // 精准怕匹配
                for (DdDiseaseIcd hubicd : hubIcds) {
                    if (hubicd.getName().equals(emricd.getIcdName())){
                        dictDiseaseIcd10.setRemark("精准匹配");
                        dictDiseaseIcd10.setHubCode(hubicd.getCode());
                        dictDiseaseIcd10.setHubName(hubicd.getName());
                        match = true;
                        break;
                    }
                }
                 if (!match){
                     // 模糊匹配
                     for (DdDiseaseIcd hubicd : hubIcds) {
                         if (FuzzyMatcher.fuzzyMatch(hubicd.getName(), emricd.getIcdName())){
                             dictDiseaseIcd10.setRemark("模糊匹配");
                             dictDiseaseIcd10.setHubCode(hubicd.getCode());
                             dictDiseaseIcd10.setHubName(hubicd.getName());
                             match = true;
                             break;
                         }
                     }
                 }

                if (!match){
                    // 查询默认
                    dictDiseaseIcd10.setRemark("未匹配到，其他类");
                    dictDiseaseIcd10.setHubCode("143");
                    dictDiseaseIcd10.setHubName("其他");
                }
                dictDiseaseIcd10Mapper.insertSelective(dictDiseaseIcd10);
            }
        }*/
        return true;
    }

    public boolean convertBb() {
        List<DictSpecimenCategory> dictSpecimenCategories = dictSpecimenCategoryMapper.selectAll();
        List<DictTemp> hubList = dictTempMapper.selectAll();
        for (DictSpecimenCategory sp : dictSpecimenCategories) {
            boolean match = false;
            for (DictTemp temp: hubList) {
                if (temp.getName().equals(sp.getEmrName())){
                    sp.setRemark("精准匹配");
                    sp.setHubCode(temp.getCode());
                    sp.setHubName(temp.getName());
                    match = true;
                    break;
                }
            }
            if (!match){
                // 模糊匹配
                for (DictTemp temp2: hubList) {
                    if (FuzzyMatcher.fuzzyMatch(temp2.getName(), sp.getEmrName())){
                        sp.setRemark("模糊匹配");
                        sp.setHubCode(temp2.getCode());
                        sp.setHubName(temp2.getName());
                        match = true;
                        break;
                    }
                }
            }
            if (!match){
                // 查询默认
                sp.setRemark("未匹配到，其他类");
                sp.setHubCode("99");
                sp.setHubName("其他");
            }
            dictSpecimenCategoryMapper.updateByPrimaryKey(sp);

        }
        return true;
    }

    public EmrActivityInfo pushOutpMr(OutpMr outpMrParam) {
        logger.info("补推门诊诊断...");
        OutpMr outpMr = outpdoctFeignClient.getOutpMrByCondition(outpMrParam).getData().get(0);

        if (StringUtils.isNotBlank(outpMr.getPatientId())){
            logger.debug("构造emrOutpatientRecord接口数据...");
            R<PatMasterIndex> medrecResult = medrecFeignClient.getPatMasterIndex(outpMr.getPatientId());
            R<ClinicMaster> outpadmResult = outpadmFeignClient.getClinicMaster(outpMr.getPatientId(), outpMr.getVisitNo(), DateUtils.dateTime(outpMr.getVisitDate()));
            if (R.SUCCESS == medrecResult.getCode() && medrecResult.getData() != null
                    && R.SUCCESS == outpadmResult.getCode() && outpadmResult.getData() != null){
                // 军队医改不推送
            /*if (outpadmResult.getData().getChargeType().equals(Constants.CHARGE_TYPE_JDYG)){
                logger.error("费别为军队医改，不推送数据");
                return;
            }*/
                // 更新推送患者信息
                hubToolService.syncPatInfo(medrecResult.getData());
                EmrOutpatientRecord emrOutpatientRecord = new EmrOutpatientRecord();
                // ID使用OUTP_MR表联合主键拼接计算MD5
                String id = DigestUtil.md5Hex(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, outpMr.getVisitDate()) + outpMr.getVisitNo());
                emrOutpatientRecord.setId(id);
                emrOutpatientRecord.setPatientId(outpMr.getPatientId());
                emrOutpatientRecord.setSerialNumber(DigestUtil.md5Hex(outpMr.getPatientId() + outpMr.getVisitNo()));
                emrOutpatientRecord.setOutpatientDate(outpMr.getVisitDate());
                emrOutpatientRecord.setInitalDiagnosisCode(String.valueOf(1)); // 初诊标识，表中没有这个字段
                emrOutpatientRecord.setChiefComplaint(outpMr.getIllnessDesc());
                emrOutpatientRecord.setPresentIllnessHis(outpMr.getMedHistory());
                emrOutpatientRecord.setPastIllnessHis(outpMr.getAnamnesis());
                emrOutpatientRecord.setOperationHis(outpMr.getMedicalRecord());
                emrOutpatientRecord.setMaritalHis(outpMr.getMarrital());
                if(StringUtils.isNotBlank(outpMr.getIndividual())){
                    emrOutpatientRecord.setAllergyHisFlag("1");
                    emrOutpatientRecord.setAllergyHis(outpMr.getIndividual());
                }
                emrOutpatientRecord.setMenstrualHis(outpMr.getMenses());
                emrOutpatientRecord.setFamilyHis(outpMr.getFamilyIll());
                emrOutpatientRecord.setPhysicalExamination(outpMr.getBodyExam());

                // 诊断代码
                OutpDiagnosisYb outpDiagnosisYb = new OutpDiagnosisYb();
                BeanUtil.copyProperties(outpMr, outpDiagnosisYb);
                R<List<OutpDiagnosisYb>> outpDiagYbResult = outpdoctFeignClient.getOutpDiagYbByCondition(outpDiagnosisYb);
                if (R.SUCCESS == outpDiagYbResult.getCode() && outpDiagYbResult.getData() != null){
                    List<OutpDiagnosisYb> outpDiagnosisYbList = outpDiagYbResult.getData();
                    List<DictDiseaseIcd10> codes = new ArrayList<>();
                    // 查不到推送其他
                    if(outpDiagnosisYbList.isEmpty()){
                        DictDiseaseIcd10 dictDiseaseIcd10 = hubToolService.getDiseaseIcd10(HubCodeEnum.DISEASE_ICD10_CODE_DEFAULT.getCode(), HubCodeEnum.DISEASE_ICD10_CODE_DEFAULT.getName());
                        codes.add(dictDiseaseIcd10);
                    }
                    for (OutpDiagnosisYb diag : outpDiagnosisYbList) {
                        DictDiseaseIcd10 dictDiseaseIcd10 = hubToolService.getDiagnosisGlIcd10(diag.getDiagCode(), diag.getDiagDesc());
                        codes.add(dictDiseaseIcd10);
                    }

                    emrOutpatientRecord.setWmDiagnosisCode(codes.stream().map(DictDiseaseIcd10::getHubCode).collect(Collectors.joining("||")));
                    emrOutpatientRecord.setWmDiagnosisName(codes.stream().map(DictDiseaseIcd10::getHubName).collect(Collectors.joining("||")));

                    if (StringUtils.isNotBlank(outpMr.getDoctor())){
                        R<Users> user = commFeignClient.getUserByName(outpMr.getDoctor());
                        if (R.SUCCESS == user.getCode() && user.getData() != null){
                            emrOutpatientRecord.setOperatorId(user.getData().getUserId());
                        }
                    }
                    if (StringUtils.isNotBlank(outpMr.getAdvice()) && outpMr.getAdvice().length() <= 100){
                        emrOutpatientRecord.setTreatment(outpMr.getAdvice());
                    }

                    PatMasterIndex patMasterIndex = medrecResult.getData();
                    emrOutpatientRecord.setPatientName(patMasterIndex.getName());
                    if (StringUtils.isBlank(patMasterIndex.getIdNo())){
                        emrOutpatientRecord.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE_OTHER.getCode());
                        emrOutpatientRecord.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE_OTHER.getName());
                        emrOutpatientRecord.setIdCard("-");
                    }else {
                        emrOutpatientRecord.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE.getCode());
                        emrOutpatientRecord.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE.getName());
                        emrOutpatientRecord.setIdCard(patMasterIndex.getIdNo());
                    }

                    ClinicMaster clinicMaster = outpadmResult.getData();
                    DictDisDept dictDisDept = hubToolService.getDept(clinicMaster.getVisitDept());

                    emrOutpatientRecord.setDeptCode(dictDisDept.getHubCode());
                    emrOutpatientRecord.setDeptName(dictDisDept.getHubName());

                    emrOutpatientRecord.setOrgCode(HubCodeEnum.ORG_CODE.getCode());
                    emrOutpatientRecord.setOrgName(HubCodeEnum.ORG_CODE.getName());
                    emrOutpatientRecord.setOperationTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getNowDate()));
//                    synchroEmrMonitorService.syncEmrOutpatientRecord(emrOutpatientRecord, httpMethod);

                    logger.debug("构造emrActivityInfo(门诊/急诊)接口数据...");
                    EmrActivityInfo emrActivityInfo = new EmrActivityInfo();
                    emrActivityInfo.setId(id);
                    emrActivityInfo.setPatientId(outpMr.getPatientId());
                    String clinicType = clinicMaster.getClinicType();
                    if (StringUtils.isNotBlank(clinicType)){
                        if (clinicType.contains("急诊")){
                            emrActivityInfo.setActivityTypeCode(HubCodeEnum.DIAGNOSIS_ACTIVITIES_EMERGENCY.getCode());
                            emrActivityInfo.setActivityTypeName(HubCodeEnum.DIAGNOSIS_ACTIVITIES_EMERGENCY.getName());
                        } else {
                            emrActivityInfo.setActivityTypeCode(HubCodeEnum.DIAGNOSIS_ACTIVITIES_OUTPATIENT.getCode());
                            emrActivityInfo.setActivityTypeName(HubCodeEnum.DIAGNOSIS_ACTIVITIES_OUTPATIENT.getName());

                        }
                    }
                    emrActivityInfo.setSerialNumber(emrOutpatientRecord.getSerialNumber());
                    emrActivityInfo.setActivityTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, outpMr.getVisitDate()));
                    String idNo = patMasterIndex.getIdNo();
                    if (StringUtils.isNotBlank(idNo)) {
                        emrActivityInfo.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE.getCode());
                        emrActivityInfo.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE.getName());
                        emrActivityInfo.setIdCard(idNo);
                    } else {
                        emrActivityInfo.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE_OTHER.getCode());
                        emrActivityInfo.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE_OTHER.getName());
                        emrActivityInfo.setIdCard("-");
                    }
                    emrActivityInfo.setPatientName(patMasterIndex.getName());

                    emrActivityInfo.setChiefComplaint(outpMr.getIllnessDesc());
                    emrActivityInfo.setPresentIllnessHis(outpMr.getMedHistory());
                    emrActivityInfo.setPhysicalExamination(outpMr.getBodyExam());
                    emrActivityInfo.setDiagnoseTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, outpMr.getVisitDate()));

                    // 诊断代码
                    if (StringUtils.isNotBlank(emrOutpatientRecord.getWmDiagnosisCode())){
                        emrActivityInfo.setWmDiseaseCode(emrOutpatientRecord.getWmDiagnosisCode());
                        emrActivityInfo.setWmDiseaseName(emrOutpatientRecord.getWmDiagnosisName());
                        // 2026-05-06新增传染病诊断条件必填
                        String[] disCodes = emrActivityInfo.getWmDiseaseCode().split("||");
                        for (String code: disCodes) {
                            DdDiseaseIcd icd10 = ddDiseaseIcdMapper.selectByCode(emrActivityInfo.getWmDiseaseCode());
                            if(icd10 != null){
                                emrActivityInfo.setDiseaseCode(StringUtils.isBlank(emrActivityInfo.getDiseaseCode()) ? code : "||" + code);
                                emrActivityInfo.setDiseaseName(StringUtils.isBlank(emrActivityInfo.getDiseaseName()) ? icd10.getName() : "||" + icd10.getName());
                            }
                        }
                    }else {
                        emrActivityInfo.setWmDiseaseCode(HubCodeEnum.DISEASE_ICD10_CODE.getCode());
                        emrActivityInfo.setWmDiseaseName(HubCodeEnum.DISEASE_ICD10_CODE.getName());
                    }
                    if (StringUtils.isNotBlank(outpMr.getDoctor())){
                        emrActivityInfo.setFillDoctor(outpMr.getDoctor());
                        emrActivityInfo.setOperatorId(emrOutpatientRecord.getOperatorId());
                    }else {
                        emrActivityInfo.setFillDoctor("-");
                        emrActivityInfo.setOperatorId("-");
                    }

                    emrActivityInfo.setDeptCode(dictDisDept.getHubCode());
                    emrActivityInfo.setDeptName(dictDisDept.getHubName());
                    emrActivityInfo.setOrgCode(HubCodeEnum.ORG_CODE.getCode());
                    emrActivityInfo.setOrgName(HubCodeEnum.ORG_CODE.getName());
                    emrActivityInfo.setOperationTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getNowDate()));
//                    synchroEmrRealService.syncEmrActivityInfo(emrActivityInfo, httpMethod);
                    return emrActivityInfo;
                } else {
                    logger.error("{}，{}对应诊断编码为空，无法同步", outpMr.getPatientId(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, outpMr.getVisitDate()));
                }
            }else {
                logger.error("{}对应PatMasterIndex信息或ClinicMaster信息为空，无法同步", outpMr.getPatientId());
            }
        }else {
            logger.error("patientId为空，无法同步");
        }
        return null;
    }

    public EmrPatientInfo pushPatMasterIndex(PatMasterIndex patMasterIndexParam) {
        logger.info("补推患者信息...");
        PatMasterIndex patMasterIndex = medrecFeignClient.getPatMasterIndex(patMasterIndexParam.getPatientId()).getData();
        logger.debug("构造emrPatientInfo接口数据...");
        // 构造请求参数
        EmrPatientInfo emrPatientInfo = new EmrPatientInfo();
        emrPatientInfo.setId(patMasterIndex.getPatientId());
        emrPatientInfo.setPatientName(patMasterIndex.getName());
        if (StringUtils.isNotBlank(patMasterIndex.getIdNo())){
            if (IdcardUtil.isValidCard(patMasterIndex.getIdNo())){
                emrPatientInfo.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE.getCode());
                emrPatientInfo.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE.getName());
                emrPatientInfo.setIdCard("-");
            }else {
                emrPatientInfo.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE_OTHER.getCode());
                emrPatientInfo.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE_OTHER.getName());
                emrPatientInfo.setIdCard(patMasterIndex.getIdNo());
            }
        }else {
            // 还获取不到，取PatientId
            emrPatientInfo.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE_OTHER.getCode());
            emrPatientInfo.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE_OTHER.getName());
            emrPatientInfo.setIdCard(patMasterIndex.getPatientId());
        }
        if(StringUtils.isNotBlank(patMasterIndex.getSex())){
            if (patMasterIndex.getSex().equals("男")){
                emrPatientInfo.setGenderCode("1");
            } else if (patMasterIndex.getSex().equals("女")) {
                emrPatientInfo.setGenderCode("2");
            } else {
                emrPatientInfo.setGenderCode(HubCodeEnum.SEX_OTHER.getCode());
            }
        }
        emrPatientInfo.setGenderName(patMasterIndex.getSex());
        emrPatientInfo.setBirthDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, patMasterIndex.getDateOfBirth()));
//        if("CN".equals(patMasterIndex.getCitizenship())){
        emrPatientInfo.setNationalityCode(HubCodeEnum.NATIONALITY_CODE.getCode());
        emrPatientInfo.setNationalityName(HubCodeEnum.NATIONALITY_CODE.getName());
//        }
        DdNation ddNation = ddNationMapper.selectByName(patMasterIndex.getNation());
        if (ddNation != null){
            emrPatientInfo.setNationCode(ddNation.getCode());
            emrPatientInfo.setNationName(ddNation.getName());
        } else {
            emrPatientInfo.setNationCode(HubCodeEnum.NATION_CODE.getCode());
            emrPatientInfo.setNationName(HubCodeEnum.NATION_CODE.getName());
        }
        emrPatientInfo.setCurrentAddrCode("-");
        emrPatientInfo.setCurrentAddrName(patMasterIndex.getMailingAddress());
        emrPatientInfo.setCurrentAddrDetail(patMasterIndex.getNextOfKinAddr());
        emrPatientInfo.setWorkunit("-");
        if (StringUtils.isNotBlank(patMasterIndex.getPhoneNumberHome())){
            emrPatientInfo.setTel(patMasterIndex.getPhoneNumberHome());
        } else if (StringUtils.isNotBlank(patMasterIndex.getNextOfKinPhone())){
            emrPatientInfo.setTel(patMasterIndex.getNextOfKinPhone());
        } else {
            emrPatientInfo.setTel("-");
        }
        Date birthDate = patMasterIndex.getDateOfBirth();
        if (null != birthDate) {
            LocalDate localDate = DateUtils.convertDateToLocalDate(birthDate);
            Period period = Period.between(localDate, LocalDate.now());
            if (period.getYears() <= 14) {
                if(patMasterIndex.getNextOfKin() != null){
                    emrPatientInfo.setContacts(patMasterIndex.getNextOfKin());
                    emrPatientInfo.setContactsTel(patMasterIndex.getNextOfKinPhone());
                }
            }
        }
        emrPatientInfo.setOrgCode(HubCodeEnum.ORG_CODE.getCode());
        emrPatientInfo.setOrgName(HubCodeEnum.ORG_CODE.getName());
        // 查询操作员ID
        if (StringUtils.isNotBlank(patMasterIndex.getOperator())){
            R<Users> user = commFeignClient.getUserByName(patMasterIndex.getOperator());
            if (R.SUCCESS == user.getCode() && user.getData() != null){
                emrPatientInfo.setOperatorId(user.getData().getUserId());
            }
        }

        emrPatientInfo.setOperationTime(DateUtils.getTime());
//        synchroEmrRealService.syncEmrPatientInfo(emrPatientInfo, httpMethod);
        return emrPatientInfo;
    }

    public EmrActivityInfo pushDiagnosis(DiagnosisKey diagnosisKey) {
        logger.info("补推住院诊断...");
        Diagnosis diagnosis = medrecFeignClient.getDiagnosis(diagnosisKey).getData();
        R<PatMasterIndex> medrecResult = medrecFeignClient.getPatMasterIndex(diagnosis.getPatientId());
        DiagnosticCategoryKey diagnosticCategoryKey = new DiagnosticCategoryKey();
        BeanUtil.copyProperties(diagnosis, diagnosticCategoryKey);
        R<DiagnosticCategory> diagnosticCatResult = medrecFeignClient.getDiagnosticCategory(diagnosticCategoryKey);
        PatVisitKey patVisitKey = new PatVisitKey();
        BeanUtil.copyProperties(diagnosis, patVisitKey);
        R<PatVisit> patVisitResult = medrecFeignClient.getPatVisit(patVisitKey);
        if (R.SUCCESS == patVisitResult.getCode() && patVisitResult.getData() != null
                && R.SUCCESS == medrecResult.getCode() && medrecResult.getData() != null){
            // 军队医改不推送
            /*if (patVisitResult.getData().getChargeType().equals(Constants.CHARGE_TYPE_JDYG)){
                logger.error("费别为军队医改，不推送数据");
                return;
            }*/
            // 更新推送患者信息
            hubToolService.syncPatInfo(medrecResult.getData());
            EmrFirstCourse emrFirstCourse = new EmrFirstCourse();
            EmrDailyCourse emrDailyCourse = new EmrDailyCourse();
            // ID使用DIAGNOSIS表patientId、visitId、diagnosisType、diagnosisNo拼接计算MD5
            String id = DigestUtil.md5Hex(diagnosis.getPatientId() + diagnosis.getVisitId() + diagnosis.getDiagnosisType() + diagnosis.getDiagnosisNo());

            if (diagnosis.getDiagnosisType().equals(Constants.DIAGNOSIS_TYPE_CODE_RYCZ) || diagnosis.getDiagnosisType().equals(Constants.DIAGNOSIS_TYPE_CODE_MZZD)){
                logger.debug("构造emrFirstCourse接口数据...");
                emrFirstCourse.setId(id);
                emrFirstCourse.setPatientId(diagnosis.getPatientId());
                emrFirstCourse.setSerialNumber(DigestUtil.md5Hex(diagnosis.getPatientId() + diagnosis.getVisitId()));
                emrFirstCourse.setCreateDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, diagnosis.getDiagnosisDate()));
                emrFirstCourse.setPresentIllnessHis(diagnosis.getDiagnosisDesc());

                emrFirstCourse.setPatientName(medrecResult.getData().getName());
                if (StringUtils.isBlank(medrecResult.getData().getIdNo())){
                    emrFirstCourse.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE_OTHER.getCode());
                    emrFirstCourse.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE_OTHER.getName());
                    emrFirstCourse.setIdCard(medrecResult.getData().getPatientId());
                }else {
                    emrFirstCourse.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE.getCode());
                    emrFirstCourse.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE.getName());
                    emrFirstCourse.setIdCard(medrecResult.getData().getIdNo());
                }

                R<PatsInHospital> hospitalResult = inpadmFeignClient.getPatsInHospital(diagnosis.getPatientId(), diagnosis.getVisitId());
                if (hospitalResult.getCode() == R.SUCCESS && hospitalResult.getData() != null){
                    emrFirstCourse.setWardNo(hospitalResult.getData().getWardCode());
                    emrFirstCourse.setBedNo(String.valueOf(hospitalResult.getData().getBedNo()));
                }

                // 治疗医生
                String doctor = null;
                if (hospitalResult.getCode() == R.SUCCESS && hospitalResult.getData() != null && StringUtils.isNotBlank(hospitalResult.getData().getDoctorInCharge())){
                    doctor = hospitalResult.getData().getDoctorInCharge();
                    R<Users> user = commFeignClient.getUserByName(hospitalResult.getData().getDoctorInCharge());
                    if (R.SUCCESS == user.getCode() && user.getData() != null){
                        emrFirstCourse.setResidentPhysicianId(user.getData().getUserId());
                        emrFirstCourse.setOperatorId(user.getData().getUserId());
                    }
                }else {
                    doctor = patVisitResult.getData().getConsultingDoctor();
                    R<Users> user = commFeignClient.getUserByName(patVisitResult.getData().getConsultingDoctor());
                    if (R.SUCCESS == user.getCode() && user.getData() != null){
                        emrFirstCourse.setResidentPhysicianId(user.getData().getUserId());
                        emrFirstCourse.setOperatorId(user.getData().getUserId());
                    }
                }

                if (diagnosticCatResult.getCode() == R.SUCCESS && diagnosticCatResult.getData() != null){
                    DictDiseaseIcd10 dictDiseaseIcd10 = hubToolService.getDiseaseIcd10(diagnosticCatResult.getData().getDiagnosisCode(), diagnosis.getDiagnosisDesc());
                    emrFirstCourse.setWmInitalDiagnosisCode(dictDiseaseIcd10.getHubCode());
                    emrFirstCourse.setWmInitalDiagnosisName(dictDiseaseIcd10.getHubName());
                }else {
                    logger.error("{}诊断编码为空，无法同步", diagnosis.getPatientId());
                    return null;
                }

                if(StringUtils.isNotBlank(diagnosis.getTreatResult())){
                    DictTreatResult dictTreatResult = dictTreatResultMapper.selectByEmrName(diagnosis.getTreatResult().trim());
                    if(dictTreatResult == null || dictTreatResult.getHubCode().equals(HubCodeEnum.TREAT_RESULT_OTHER.getCode())){
                        emrFirstCourse.setDiseaseProgressionCode(HubCodeEnum.TREAT_RESULT_OTHER.getCode());
                        emrFirstCourse.setDiseaseProgressionName(diagnosis.getTreatResult());
                    }else {
                        emrFirstCourse.setDiseaseProgressionCode(dictTreatResult.getHubCode());
                        emrFirstCourse.setDiseaseProgressionName(dictTreatResult.getHubName());
                    }
                }

                DictDisDept dictDisDept = hubToolService.getDept(patVisitResult.getData().getDeptAdmissionTo());

                emrFirstCourse.setDeptCode(dictDisDept.getHubCode());
                emrFirstCourse.setDeptName(dictDisDept.getHubName());

                emrFirstCourse.setOrgCode(HubCodeEnum.ORG_CODE.getCode());
                emrFirstCourse.setOrgName(HubCodeEnum.ORG_CODE.getName());
                emrFirstCourse.setOperationTime(DateUtils.getTime());

//                synchroEmrMonitorService.syncEmrFirstCourse(emrFirstCourse, httpMethod);

                logger.debug("构造emrActivityInfo(首次病程)接口数据...");
                EmrActivityInfo emrActivityInfo = new EmrActivityInfo();
                emrActivityInfo.setId(id);
                emrActivityInfo.setPatientId(emrFirstCourse.getPatientId());
                emrActivityInfo.setActivityTypeCode(HubCodeEnum.DIAGNOSIS_ACTIVITIES_FIRST_COURSE.getCode());
                emrActivityInfo.setActivityTypeName(HubCodeEnum.DIAGNOSIS_ACTIVITIES_FIRST_COURSE.getName());
                emrActivityInfo.setSerialNumber(emrFirstCourse.getSerialNumber());
                emrActivityInfo.setActivityTime(emrFirstCourse.getCreateDate());
                emrActivityInfo.setIdCardTypeCode(emrFirstCourse.getIdCardTypeCode());
                emrActivityInfo.setIdCardTypeName(emrFirstCourse.getIdCardTypeName());
                emrActivityInfo.setIdCard(emrFirstCourse.getIdCard());
                emrActivityInfo.setPatientName(emrFirstCourse.getPatientName());
                emrActivityInfo.setChiefComplaint(emrFirstCourse.getChiefComplaint());
                emrActivityInfo.setPresentIllnessHis(emrFirstCourse.getPresentIllnessHis());
                emrActivityInfo.setDiagnoseTime(emrFirstCourse.getCreateDate());
                emrActivityInfo.setWmDiseaseCode(emrFirstCourse.getWmInitalDiagnosisCode());
                emrActivityInfo.setWmDiseaseName(emrFirstCourse.getWmInitalDiagnosisName());
                emrActivityInfo.setFillDoctor(doctor);
                emrActivityInfo.setOperatorId(emrFirstCourse.getOperatorId());
                emrActivityInfo.setDeptCode(emrFirstCourse.getDeptCode());
                emrActivityInfo.setDeptName(emrFirstCourse.getDeptName());
                emrActivityInfo.setOrgCode(emrFirstCourse.getOrgCode());
                emrActivityInfo.setOrgName(emrFirstCourse.getOrgName());
                emrActivityInfo.setOperationTime(DateUtils.getTime());
//                synchroEmrRealService.syncEmrActivityInfo(emrActivityInfo, httpMethod);
                return emrActivityInfo;


            }else
//                if (diagnosis.getDiagnosisType().equals(Constants.DIAGNOSIS_TYPE_CODE_QT) && diagnosis.getVisitId() != null)
            {
                logger.debug("构造emrDailyCourse接口数据...");
                emrDailyCourse.setId(id);
                emrDailyCourse.setPatientId(diagnosis.getPatientId());
                emrDailyCourse.setSerialNumber(DigestUtil.md5Hex(diagnosis.getPatientId() + diagnosis.getVisitId()));
                emrDailyCourse.setCreateDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, diagnosis.getDiagnosisDate()));
                emrDailyCourse.setCourse(diagnosis.getDiagnosisDesc());

                emrDailyCourse.setPatientName(medrecResult.getData().getName());
                if (StringUtils.isBlank(medrecResult.getData().getIdNo())){
                    emrDailyCourse.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE_OTHER.getCode());
                    emrDailyCourse.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE_OTHER.getName());
                    emrDailyCourse.setIdCard(medrecResult.getData().getIdNo());
                }else {
                    emrDailyCourse.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE.getCode());
                    emrDailyCourse.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE.getName());
                    emrDailyCourse.setIdCard(medrecResult.getData().getIdNo());
                }
                R<Users> user = commFeignClient.getUserByName(patVisitResult.getData().getConsultingDoctor());
                if (R.SUCCESS == user.getCode() && user.getData() != null){
                    emrDailyCourse.setOperatorId(user.getData().getUserId());
                }

                DictDisDept dictDisDept = hubToolService.getDept(patVisitResult.getData().getDeptAdmissionTo());

                emrDailyCourse.setDeptCode(dictDisDept.getHubCode());
                emrDailyCourse.setDeptName(dictDisDept.getHubName());

                emrDailyCourse.setOrgCode(HubCodeEnum.ORG_CODE.getCode());
                emrDailyCourse.setOrgName(HubCodeEnum.ORG_CODE.getName());
                emrDailyCourse.setOperationTime(DateUtils.getTime());

                if(StringUtils.isNotBlank(diagnosis.getTreatResult())){
                    DictTreatResult dictTreatResult = dictTreatResultMapper.selectByEmrName(diagnosis.getTreatResult().trim());
                    if(dictTreatResult == null || dictTreatResult.getHubCode().equals(HubCodeEnum.TREAT_RESULT_OTHER.getCode())){
                        emrDailyCourse.setDiseaseProgressionCode(HubCodeEnum.TREAT_RESULT_OTHER.getCode());
                        emrDailyCourse.setDiseaseProgressionName(diagnosis.getTreatResult());
                    }else {
                        emrDailyCourse.setDiseaseProgressionCode(dictTreatResult.getHubCode());
                        emrDailyCourse.setDiseaseProgressionName(dictTreatResult.getHubName());
                    }
                }

//                synchroEmrMonitorService.syncEmrDailyCourse(emrDailyCourse, httpMethod);

                logger.debug("构造emrActivityInfo(日常病程)接口数据...");
                EmrActivityInfo emrActivityInfo = new EmrActivityInfo();
                emrActivityInfo.setId(id);
                emrActivityInfo.setPatientId(emrDailyCourse.getPatientId());
                emrActivityInfo.setActivityTypeCode(HubCodeEnum.DIAGNOSIS_ACTIVITIES_DAILY_COURSE.getCode());
                emrActivityInfo.setActivityTypeName(HubCodeEnum.DIAGNOSIS_ACTIVITIES_DAILY_COURSE.getName());
                emrActivityInfo.setSerialNumber(emrDailyCourse.getSerialNumber());
                emrActivityInfo.setActivityTime(emrDailyCourse.getCreateDate());
                emrActivityInfo.setIdCardTypeCode(emrDailyCourse.getIdCardTypeCode());
                emrActivityInfo.setIdCardTypeName(emrDailyCourse.getIdCardTypeName());
                emrActivityInfo.setIdCard(emrDailyCourse.getIdCard());
                emrActivityInfo.setPatientName(emrDailyCourse.getPatientName());
                emrActivityInfo.setDiagnoseTime(emrDailyCourse.getCreateDate());
                if (diagnosticCatResult.getCode() == R.SUCCESS && diagnosticCatResult.getData() != null){
                    DictDiseaseIcd10 dictDiseaseIcd10 = hubToolService.getDiseaseIcd10(diagnosticCatResult.getData().getDiagnosisCode(), diagnosis.getDiagnosisDesc());
                    emrActivityInfo.setWmDiseaseCode(dictDiseaseIcd10.getHubCode());
                    emrActivityInfo.setWmDiseaseName(dictDiseaseIcd10.getHubName());
                }else {
                    logger.error("{}诊断编码为空，无法同步", diagnosis.getPatientId());
                    return null;
                }
                emrActivityInfo.setFillDoctor(patVisitResult.getData().getConsultingDoctor());
                emrActivityInfo.setOperatorId(emrDailyCourse.getOperatorId());
                if (StringUtils.isBlank(emrDailyCourse.getOperatorId())){
                    emrActivityInfo.setOperatorId("-");
                }
                emrActivityInfo.setDeptCode(emrDailyCourse.getDeptCode());
                emrActivityInfo.setDeptName(emrDailyCourse.getDeptName());
                emrActivityInfo.setOrgCode(emrDailyCourse.getOrgCode());
                emrActivityInfo.setOrgName(emrDailyCourse.getOrgName());
                emrActivityInfo.setOperationTime(DateUtils.getTime());
//                synchroEmrRealService.syncEmrActivityInfo(emrActivityInfo, httpMethod);
                return emrActivityInfo;
            }

        }else {
            logger.error("{}对应PatMasterIndex或PatVisit信息为空，无法同步", diagnosis.getPatientId());
        }
        return null;
    }

    public boolean pushLabTestMaster(LabTestMaster labTestMaster) {
        logger.info("补推检验信息...");
        labTestMaster = labFeignClient.getLabTestMaster(labTestMaster.getTestNo()).getData();
        if(labTestMaster == null || StringUtils.isBlank(labTestMaster.getResultStatus()) || !"4".equals(labTestMaster.getResultStatus())){
            logger.error("检查报告未确认，无法同步");
            return false;
        }

        R<List<LabResult>> resultItemsResult = labFeignClient.getResultByTestNo(labTestMaster.getTestNo());
        R<PatMasterIndex> medrecResult = medrecFeignClient.getPatMasterIndex(labTestMaster.getPatientId());
        if (R.SUCCESS == medrecResult.getCode() && medrecResult.getData() != null
                && R.SUCCESS == resultItemsResult.getCode() && !resultItemsResult.getData().isEmpty()){
            // 更新推送患者信息
            hubToolService.syncPatInfo(medrecResult.getData());
            DictDisDept dept = new DictDisDept();
            dept.setStatus(Constants.STATUS_NORMAL);
            dept.setIsDefault(Constants.IS_DEFAULT);
            DictDisDept dictDisDeptDefault = dictDisDeptMapper.selectByCondition(dept);

            for (LabResult labResult : resultItemsResult.getData()) {
                logger.debug("构造emrExLab接口数据...");
                EmrExLab emrExLab = new EmrExLab();
                EmrExLabItem emrExLabItem = new EmrExLabItem();
                // ID使用LAB_RESULT表联合主键拼接计算MD5
                String id = DigestUtil.md5Hex(labResult.getTestNo() + labResult.getItemNo() + labResult.getPrintOrder());
                emrExLab.setId(id);
                emrExLab.setApplicationFormNo(String.valueOf(labResult.getItemNo()));
                emrExLab.setExaminationDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, labTestMaster.getResultsRptDateTime()));
                emrExLab.setExaminationReportDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, labResult.getResultDateTime()));
                emrExLab.setExaminationReportNo(id);
                emrExLab.setExaminationObjectiveDesc(labResult.getResult());
                emrExLab.setExaminationSubjectiveDesc(labResult.getResult());
                emrExLab.setExaminationNotes(labResult.getReportItemName());

                emrExLab.setPatientId(labTestMaster.getPatientId());
                if("1".equals(labTestMaster.getPatientSource()) || labTestMaster.getVisitNo() != null ){
                    emrExLab.setActivityTypeCode(HubCodeEnum.DIAGNOSIS_ACTIVITIES_OUTPATIENT.getCode());
                    emrExLab.setActivityTypeName(HubCodeEnum.DIAGNOSIS_ACTIVITIES_OUTPATIENT.getName());
                    emrExLab.setSerialNumber(DigestUtil.md5Hex(labTestMaster.getPatientId() + labTestMaster.getVisitNo()));
                }else if("2".equals(labTestMaster.getPatientSource()) || labTestMaster.getVisitId() != null ){
                    emrExLab.setActivityTypeCode(HubCodeEnum.DIAGNOSIS_ACTIVITIES_HOSPITALIZATION.getCode());
                    emrExLab.setActivityTypeName(HubCodeEnum.DIAGNOSIS_ACTIVITIES_HOSPITALIZATION.getName());
                    emrExLab.setSerialNumber(DigestUtil.md5Hex(labTestMaster.getPatientId() + labTestMaster.getVisitId()));
                    R<PatsInHospital> hospitalResult = inpadmFeignClient.getPatsInHospital(labTestMaster.getPatientId(), labTestMaster.getVisitId().shortValue());
                    if (R.SUCCESS == hospitalResult.getCode() && hospitalResult.getData() != null){
                        emrExLab.setWardNo(hospitalResult.getData().getWardCode());
                        emrExLab.setBedNo(String.valueOf(hospitalResult.getData().getBedNo()));
                    }
                }else {
                    logger.error("PATIENT_SOURCE:{}, 非门诊和住院，无法同步", labTestMaster.getPatientSource());
                    return false;
                }
                emrExLab.setApplyOrgCode(HubCodeEnum.ORG_CODE.getCode());
                emrExLab.setApplyOrgName(HubCodeEnum.ORG_CODE.getName());
                emrExLab.setOrgCode(HubCodeEnum.ORG_CODE.getCode());
                emrExLab.setOrgName(HubCodeEnum.ORG_CODE.getName());
                if(StringUtils.isNotBlank(labTestMaster.getOrderingDept())){
                    DictDisDept dictDisDept = hubToolService.getDept(labTestMaster.getOrderingDept());
                    emrExLab.setApplyDeptCode(dictDisDept.getHubCode());
                    emrExLab.setApplyDeptName(dictDisDept.getHubName());
                }
                R<Users> user = commFeignClient.getUserByName(labTestMaster.getOrderingProvider());
                if (R.SUCCESS == user.getCode() && user.getData() != null){
                    emrExLab.setApplyPhysicianId(user.getData().getUserId());
                }
                emrExLab.setExaminationReportDate(DateUtils.dateTime(labResult.getResultDateTime()));
                DictSpecimenCategory dictSpecimenCategory = dictSpecimenCategoryMapper.selectByEmrName(labTestMaster.getSpecimen().trim());
                if(dictSpecimenCategory == null){
                    emrExLab.setSpecimenCategoryCode(HubCodeEnum.PAY_TYPE_OTHER.getCode());
                    emrExLab.setSpecimenCategoryName(labTestMaster.getSpecimen());
                }else {
                    emrExLab.setSpecimenCategoryCode(dictSpecimenCategory.getHubCode());
                    emrExLab.setSpecimenCategoryName(dictSpecimenCategory.getHubName());
                }
                if (labTestMaster.getSpcmSampleDateTime()!= null && labTestMaster.getSpcmReceivedDateTime() != null){
                    // 标本号
                    String specimenNo = DigestUtil.md5Hex(labTestMaster.getSpecimen() +
                            DateUtils.getYyyyMMddHHmmssString(labTestMaster.getSpcmSampleDateTime()) + DateUtils.getYyyyMMddHHmmssString(labTestMaster.getSpcmReceivedDateTime()));
                    emrExLab.setSpecimenNo(specimenNo);
                    emrExLab.setSpecimenSamplingDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, labTestMaster.getSpcmSampleDateTime()));
                    emrExLab.setSpecimenReceivingDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, labTestMaster.getSpcmReceivedDateTime()));
                }
                if(StringUtils.isNotBlank(labTestMaster.getTranscriptionist())){
                    R<Users> usero = commFeignClient.getUserByName(labTestMaster.getOrderingProvider());
                    if (R.SUCCESS == usero.getCode() && usero.getData() != null){
                        emrExLab.setExaminationPhysicianId(usero.getData().getUserId());
                        emrExLab.setExaminationReportId(usero.getData().getUserId());
                        emrExLab.setOperatorId(usero.getData().getUserId());
                    }
                }else {
                    emrExLab.setExaminationPhysicianId("-");
                    emrExLab.setExaminationReportId("-");
                    emrExLab.setOperatorId("-");
                }
                if (StringUtils.isNotBlank(labTestMaster.getPerformedBy())){
                    DictDisDept dictDisDept = hubToolService.getDept(labTestMaster.getPerformedBy());
                    emrExLab.setDeptCode(dictDisDept.getHubCode());
                    emrExLab.setDeptName(dictDisDept.getHubName());
                }else {
                    emrExLab.setDeptCode(dictDisDeptDefault.getHubCode());
                    emrExLab.setDeptName(dictDisDeptDefault.getHubName());
                }
                emrExLab.setOperationTime(DateUtils.getTime());

                emrExLab.setPatientName(medrecResult.getData().getName());
                if (StringUtils.isBlank(medrecResult.getData().getIdNo())){
                    emrExLab.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE_OTHER.getCode());
                    emrExLab.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE_OTHER.getName());
                    emrExLab.setIdCard("-");
                }else {
                    emrExLab.setIdCardTypeCode(HubCodeEnum.ID_CARD_TYPE.getCode());
                    emrExLab.setIdCardTypeName(HubCodeEnum.ID_CARD_TYPE.getName());
                    emrExLab.setIdCard(medrecResult.getData().getIdNo());
                }
                synchroEmrMonitorService.syncEmrExLab(emrExLab, "POST");

                logger.debug("构造emrExLabItem接口数据...");
                emrExLabItem.setId(id);
                emrExLabItem.setExLabId(id);

                // 处理检验项目代码为空的情况
                String itemCode = labResult.getReportItemCode();
                if(StringUtils.isBlank(itemCode)){
                    itemCode = "-";
                }
                emrExLabItem.setItemCode(itemCode);
                emrExLabItem.setItemName(labResult.getReportItemName());

                if(StringUtils.isNotBlank(labResult.getResult())){
                    if (Validator.hasChinese(labResult.getResult())){
                        // 定性
                        emrExLabItem.setSourceExaminationResultCode(DigestUtil.md5Hex(labResult.getResult()));
                        emrExLabItem.setSourceExaminationResultCode(labResult.getResult());
                        List<DdExQuantification> ddExQuantifications = ddExQuantificationMapper.selectAll();
                        DdExQuantification ddExQuantification = null;
                        for (DdExQuantification dd : ddExQuantifications) {
                            if (labResult.getResult().equals(dd.getName())){
                                ddExQuantification = dd;
                                break;
                            }
                            if (labResult.getResult().startsWith(dd.getName())){
                                ddExQuantification = dd;
                                break;
                            }
                        }
//                        ddExQuantification = ddExQuantificationMapper.selectByName(labResult.getResult());
                        if (ddExQuantification == null){
                            emrExLabItem.setExaminationResultCode("07");
                            emrExLabItem.setExaminationResultName(labResult.getResult());
                        }else {
                            emrExLabItem.setExaminationResultCode(ddExQuantification.getCode());
                            emrExLabItem.setExaminationResultName(ddExQuantification.getName());
                        }
                    }else {
                        if (labResult.getResult().equals("-")){
                            emrExLabItem.setSourceExaminationResultCode(DigestUtil.md5Hex(labResult.getResult()));
                            emrExLabItem.setSourceExaminationResultCode(labResult.getResult());
                            emrExLabItem.setExaminationResultCode("02");
                            emrExLabItem.setExaminationResultName("阴性");
                        }else if(labResult.getResult().equals("+")){
                            emrExLabItem.setSourceExaminationResultCode(DigestUtil.md5Hex(labResult.getResult()));
                            emrExLabItem.setSourceExaminationResultCode(labResult.getResult());
                            emrExLabItem.setExaminationResultCode("01");
                            emrExLabItem.setExaminationResultName("阳性");
                        }else {
                            // 定量
                            emrExLabItem.setExaminationQuantification(labResult.getResult());
                            emrExLabItem.setExaminationQuantificationUnit(labResult.getUnits());
                            if (StringUtils.isNotBlank(labResult.getAbnormalIndicator())){
                                if (labResult.getAbnormalIndicator().equals("H")){
                                    emrExLabItem.setExaminationQuantificationRi("2");
                                }else if (labResult.getAbnormalIndicator().equals("L")){
                                    emrExLabItem.setExaminationQuantificationRi("1");
                                }else {
                                    emrExLabItem.setExaminationQuantificationRi("0");
                                }
                            }else {
                                emrExLabItem.setExaminationQuantificationRi("0");
                            }

                            if (labResult.getPrintContext().contains("健康非妊娠绝经前女性")){
                                emrExLabItem.setExaminationQuantificationLower("0");
                                emrExLabItem.setExaminationQuantificationUpper("7184");
                            }else {
                                String between = StrUtil.removeAll(labResult.getPrintContext(), "");
                                String[] betweens = between.split("-");
                                if (betweens.length == 1){
                                    emrExLabItem.setExaminationQuantificationLower(betweens[0].substring(0, 15));
                                    emrExLabItem.setExaminationQuantificationUpper(betweens[0].substring(0, 15));
                                } else {
                                    emrExLabItem.setExaminationQuantificationLower(betweens[0].substring(0, 15));
                                    emrExLabItem.setExaminationQuantificationUpper(betweens[1].substring(0, 15));
                                }
                            }
                        }
                    }
                }else {
                    emrExLabItem.setSourceExaminationResultCode(UUID.fastUUID().toString());
                    emrExLabItem.setSourceExaminationResultCode("无");
                    emrExLabItem.setExaminationResultCode("07");
                    emrExLabItem.setExaminationResultName("未检出");
                }

                emrExLabItem.setOperatorId(emrExLab.getOperatorId());
                emrExLabItem.setOperationTime(DateUtils.getTime());
                synchroEmrMonitorService.syncEmrExLabItem(emrExLabItem, "POST");
                return true;
            }
        }else {
            logger.error("{}PatMasterIndex或LabResultVo信息为空或报告未确认，无法同步", labTestMaster.getTestNo());
        }
        return false;
    }
}
