package com.whaler.oasys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.ReportMapper;
import com.whaler.oasys.model.entity.ReportEntity;
import com.whaler.oasys.service.ReportService;

@Service
public class ReportServiceImpl
extends ServiceImpl<ReportMapper, ReportEntity>
implements ReportService {
    
}
