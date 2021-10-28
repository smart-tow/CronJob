package com.cs.crond.watcher.listener;

import com.cs.crond.watcher.service.WatherThread;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

public class ReadyEventListener implements ApplicationListener<ApplicationReadyEvent>
{
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent)
    {
        ConfigurableApplicationContext context = applicationReadyEvent.getApplicationContext();

        WatherThread watherService = context.getBean(WatherThread.class);
        watherService.start();

    }
}
