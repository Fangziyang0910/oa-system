package com.whaler.oasys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.ApplicantEntity;
import com.whaler.oasys.model.vo.ApplicantVo;

public interface ApplicantService
extends IService<ApplicantEntity> {
    public int insertApplicantEntity(Long applicantId, String processinstanceId);

    public int deleteApplicantEntity(Long applicantId, String processinstanceId);
    public ApplicantVo selectByApplicantId(Long applicantId);
}
