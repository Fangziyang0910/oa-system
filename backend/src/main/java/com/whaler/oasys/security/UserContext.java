package com.whaler.oasys.security;

public final class UserContext {
    private UserContext(){};
    
    private static final ThreadLocal<Long> userId=new ThreadLocal<>();
}
