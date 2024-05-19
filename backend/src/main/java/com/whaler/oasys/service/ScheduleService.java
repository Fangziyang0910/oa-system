package com.whaler.oasys.service;

public interface ScheduleService {
    void dailyScheduledTask();

    void weeklyScheduledTask();

    void adminBoardScheduledTask();

    void dueWarningTask();
}
