package com.cs.crond.watcher.Job;
/**
 * url任务job
 *
 */

import com.cs.crond.watcher.service.QuartzTask;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class UrlJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(UrlJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Map jobMap = jobExecutionContext.getJobDetail().getJobDataMap();

//        System.out.println("执行："+jobMap.get("content")+" "+jobExecutionContext.getFireTime());

        try {
            String result= client(jobMap.get("content").toString());
            log.info("执行："+jobMap.get("content")+" "+jobExecutionContext.getFireTime());
        }catch (Exception e) {
            e.printStackTrace();
            log.error("执行："+jobMap.get("content")+"报错："+e.getMessage());
        }
//        JobStoreTX j=new JobStoreTX();
//        log.info(j.getDataSource()+"------------------");

//        System.out.println(result);
//        System.out.println("下次执行："+jobMap.get("content")+" "+jobExecutionContext.getNextFireTime());
//        System.out.println("<><><><><><><><><><><><><><><><><>");

    }
    public String client(String url){


        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
//        httpRequestFactory.setConnectionRequestTimeout(10000);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(1000*60*2);

        RestTemplate client = new RestTemplate(httpRequestFactory);

        ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, null, String.class);
        return response.getBody();
    }

}
