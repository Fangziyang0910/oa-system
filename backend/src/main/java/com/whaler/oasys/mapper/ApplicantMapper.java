package com.whaler.oasys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.ApplicantEntity;

@Repository
public interface ApplicantMapper
extends BaseMapper<ApplicantEntity> {
    /**
     * 插入申请人实体信息。
     * 
     * @param processInstanceId 流程实例ID，用于标识具体的流程实例。
     * @param operatorId 操作人ID，标识进行此操作的用户。
     * 
     * @return 返回插入的记录数。
     */
    int insertApplicantEntity(
        @Param("processInstanceId") String processInstanceId,
        @Param("operatorId") Long operatorId
    );

    /**
     * 根据申请人ID查询申请人实体列表。
     * 
     * @param applicantId 申请人的唯一标识符。
     * @return 返回一个申请人实体列表，如果申请人不存在，则返回空列表。
     */
    List<ApplicantEntity> selectByApplicantId(String applicantId);
}