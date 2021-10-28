package com.cs.crond.watcher.service;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class QuartzTask {

    private static final Logger log = LoggerFactory.getLogger(QuartzTask.class);
    @Autowired
    private Scheduler scheduler;


    /**
     * 添加任务
     * @param jobClass job类
     * @param jobName job名称
     * @param jobGroupName 组名
     * @param content 内容
     * @param cronExpression
     */
    public void addJob(Class jobClass, String jobName, String jobGroupName, String cronExpression, String content) {

        try {
            // 启动调度器
//            scheduler.start();

            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName,jobGroupName).build();
            Map jobMap = jobDetail.getJobDataMap();
            jobMap.put("content",content);

//            System.out.println("工作时间："+cronExpression);
            // 表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression.trim());

            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName).withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("成功添加："+jobName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加："+jobName+"失败=="+e.getMessage());

        }

    }

    /**
     * 更新定时任务
     * @param jobsName
     * @param jobGroupName
     * @param cronExpression
     * @return
     * @throws Exception
     */

    public void updateJob(String jobsName, String jobGroupName, String cronExpression) {

        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobsName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     * 删除定时任务
     * @param jobName
     * @param jobGroupName
     * @return
     * @throws Exception
     */

    public void deleteJob(String jobName, String jobGroupName) {

        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));

            log.info("成功删除："+jobName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除："+jobName+"失败=="+e.getMessage());
        }
    }


}
