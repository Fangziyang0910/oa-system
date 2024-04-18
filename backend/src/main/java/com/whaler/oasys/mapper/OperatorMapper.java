package com.whaler.oasys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.OperatorEntity;

@Repository
@Mapper
public interface OperatorMapper
extends BaseMapper<OperatorEntity> {
    /**
     * 插入操作员实体
     * 
     * @param processInstanceId 流程实例ID，用于标识特定的流程实例
     * @param operatorId 操作员ID，用于标识进行操作的用户
     * @return 返回插入操作的影响行数，通常为1表示插入成功
     */
    int insertOperatorEntity(
        @Param("operatorId") Long operatorId,
        @Param("processinstanceId") String processinstanceId
    );

    int deleteOperatorEntity(
        @Param("operatorId") Long operatorId,
        @Param("processinstanceId") String processinstanceId
    );

    /**
     * 根据操作员ID选择操作员实体。
     * 
     * @param operatorId 操作员的唯一标识符。
     * @return 返回匹配的操作员实体，如果没有找到，则返回null。
     */
    List<OperatorEntity> selectByOperatorId(Long operatorId);
}
