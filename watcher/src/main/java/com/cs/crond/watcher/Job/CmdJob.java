package com.cs.crond.watcher.Job;
/**
 * cmd 命令job
 */

import com.cs.crond.watcher.service.QuartzTask;
import lombok.extern.java.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CmdJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(CmdJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Map jobMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String shell = jobMap.get("content").toString();

        try {
            List result=runShell(shell);
//            System.out.println(result);
            log.info("运行脚本 "+shell);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("运行脚本 "+shell+"  失败："+e.getMessage());
        }
//        JobStoreTX j=new JobStoreTX();
//        log.info(j.getDataSource()+"------------------");
    }


    /**
     * 运行shell
     *
     * @param shStr
     *            需要执行的shell
     * @return
     * @throws Exception
     */
    public static List runShell(String shStr) throws Exception {
        List<String> strList = new ArrayList();

        Process process;

        process=Runtime.getRuntime().exec(new String[]{"/bin/bash","-c",shStr},null,null);
        process.waitFor(30,TimeUnit.SECONDS);
        if(process.exitValue()!=0){
            process.destroyForcibly();
        }

//        InputStreamReader ir = new InputStreamReader(process
//                .getInputStream());
//        LineNumberReader input = new LineNumberReader(ir);
//        String line;
//        process.waitFor();
//        while ((line = input.readLine()) != null){
////            System.out.println(line);
//            strList.add(line);
//        }

        return strList;
    }
}
