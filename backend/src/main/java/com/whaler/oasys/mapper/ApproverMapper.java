package com.whaler.oasys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.ApproverEntity;

@Repository
@Mapper
public interface ApproverMapper
extends BaseMapper<ApproverEntity> {
    /**
     * 插入审批人实体信息到数据库。
     * 
     * @param approverId 审批人的唯一标识符，通常为用户ID。
     * @param processInstanceId 流程实例的唯一标识符，用于关联到具体的流程中。
     * @return 返回插入操作影响的数据库行数，通常为1表示插入成功。
     */
    int insertApproverEntity(
        @Param("approverId") Long approverId,
        @Param("processinstanceId") String processinstanceId
    );

    /**
     * 删除审批实体
     * 
     * @param approverId 审批者ID，用于标识要删除的审批者的唯一ID。
     * @param processinstanceId 流程实例ID，用于指定删除操作的上下文，即在哪个流程实例中进行删除。
     * @return 返回删除操作影响的行数，通常为1表示成功删除一个实体，若为0则表示未找到相关实体。
     */
    int deleteApproverEntity(
        @Param("approverId") Long approverId,
        @Param("processinstanceId") String processinstanceId
    );

    /**
     * 根据审批人ID选择对应的审批实体列表。
     * 
     * @param approverId 审批人的唯一标识符。
     * @return 返回一个审批实体列表，这些实体与给定的审批人ID相关联。
     */
    List<ApproverEntity> selectByApproverId(Long approverId);
}
