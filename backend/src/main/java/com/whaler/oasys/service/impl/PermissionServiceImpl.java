package com.whaler.oasys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.PermissionMapper;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.service.PermissionService;

@Service
public class PermissionServiceImpl
extends ServiceImpl<PermissionMapper, PermissionEntity>
implements PermissionService {
    
}
