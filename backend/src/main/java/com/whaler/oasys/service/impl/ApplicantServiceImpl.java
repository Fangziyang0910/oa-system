package com.whaler.oasys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.ApplicantMapper;
import com.whaler.oasys.model.entity.ApplicantEntity;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.service.ApplicantService;

@Service
public class ApplicantServiceImpl
extends ServiceImpl<ApplicantMapper,ApplicantEntity>
implements ApplicantService {
    @Override
    public int insertApplicantEntity(Long applicantId, String processinstanceId) {
        return this.baseMapper.insertApplicantEntity(applicantId, processinstanceId);
    }

    @Override
    public int deleteApplicantEntity(Long applicantId, String processinstanceId) {
        return this.baseMapper.deleteApplicantEntity(applicantId, processinstanceId);
    }

    @Override
    public ApplicantVo selectByApplicantId(Long applicantId) {
        List<ApplicantEntity> applicantEntities = this.baseMapper.selectByApplicantId(applicantId);
        ApplicantVo applicantVo = new ApplicantVo();
        applicantVo.setApplicantId(applicantId);
        applicantVo.setProcessinstanceIds(
            applicantEntities.stream().map(ApplicantEntity::getProcessinstanceId)
            .collect(Collectors.toSet())
        );
        return applicantVo;
    }
}
