package com.whaler.oasys.service;

/**
 * ScheduleService接口定义了各种定时任务的方法。
 * 该接口不包含任何参数和返回值，因为这些方法的实现将会具体依赖于调用它们的上下文。
 */
public interface ScheduleService {
    
    /**
     * 执行每日定时任务。
     * 该方法没有参数和返回值。
     * 实现该方法的类应该根据具体需求定义每日任务的内容。
     */
    void dailyScheduledTask();

    /**
     * 执行每周定时任务。
     * 该方法没有参数和返回值。
     * 实现该方法的类应该根据具体需求定义每周任务的内容。
     */
    void weeklyScheduledTask();

    /**
     * 执行管理面板定时任务。
     * 该方法没有参数和返回值。
     * 实现该方法的类应该根据具体需求定义管理面板上的定时任务内容。
     */
    void adminBoardScheduledTask();

    /**
     * 执行到期警告任务。
     * 该方法没有参数和返回值。
     * 实现该方法的类应该根据具体需求定义到期警告的任务内容。
     */
    void dueWarningTask();
}
