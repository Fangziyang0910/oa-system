package com.whaler.oasys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.ReportEntity;

@Repository
@Mapper
public interface ReportMapper
extends BaseMapper<ReportEntity> {
    
}
