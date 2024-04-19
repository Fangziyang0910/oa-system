package com.whaler.oasys.security;

/**
 * 用户上下文环境类，用于管理当前线程的用户ID。
 */
public final class UserContext {
    // 私有构造方法，防止实例化
    private UserContext(){}; 
    
    // 利用ThreadLocal为每个线程维护自己的用户ID
    private static final ThreadLocal<Long> userId=new ThreadLocal<>(); 

    /**
     * 设置当前线程的用户ID。
     * 
     * @param id 需要设置的用户ID
     */
    public static void setCurrentUserId(Long id){
        userId.set(id);
    }

    /**
     * 获取当前线程的用户ID。
     * 
     * @return 当前线程的用户ID
     */
    public static Long getCurrentUserId(){
        return userId.get();
    }
    
    /**
     * 清除当前线程的用户ID，使其在当前线程后续的操作中无法获取到用户ID。
     */
    public static void clear(){
        userId.remove();
    }
}
