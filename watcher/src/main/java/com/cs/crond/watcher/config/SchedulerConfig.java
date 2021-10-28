package com.cs.crond.watcher.config;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig implements SchedulerFactoryBeanCustomizer {

    @Override
    public void customize(org.springframework.scheduling.quartz.SchedulerFactoryBean schedulerFactoryBean) {
        schedulerFactoryBean.setStartupDelay(2);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
    }
}
