package com.whaler.oasys.service.impl;

import org.flowable.engine.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whaler.oasys.service.ScheduleService;

@Service
public class ScheduleServiceImpl
implements ScheduleService {
    @Autowired
    private HistoryService historyService;

    
}
