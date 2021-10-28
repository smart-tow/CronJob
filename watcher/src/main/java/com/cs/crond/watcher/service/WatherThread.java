package com.cs.crond.watcher.service;

import com.cs.crond.watcher.Job.CmdJob;
import com.cs.crond.watcher.Job.UrlJob;
import com.cs.crond.watcher.entity.ExeObj;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

@Service
public class WatherThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(WatherThread.class);
    @Value("${monitor.path}")
    private String path;
    @Autowired
    private QuartzTask quartzTask;

    private WatchService watchService = null;


    @Override
    public void run() {
        try {
            //初始化所有任务
            readAll();

            System.out.println(this.path);
            Path path = FileSystems.getDefault().getPath(this.path);
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        while (true) {

            WatchKey watchKey =null;
            try {

                watchKey = watchService.take();
                for (WatchEvent event : watchKey.pollEvents()) {

                    //得到 监听的事件类型
                    WatchEvent.Kind kind = event.kind();
                    //得到 监听的文件/目录的路径
                    Path pathName = (Path) event.context();
                    //下面要做具体的功能
                    //先判断对文件操作类型（增删改） 如果是删除文件则直接删除job 修改文件 删删除job再添加job
                    //kind.type  	ENTRY_CREATE 新增 ENTRY_DELETE 删除 ENTRY_MODIFY 修改 OVERFLOW 覆盖
                    //job名称用文件名
                    String jobName = pathName.getFileName().toString();
                    if(kind!=StandardWatchEventKinds.ENTRY_CREATE){
                        //非创建
                        quartzTask.deleteJob(jobName,Scheduler.DEFAULT_GROUP);

                    }
                    if(kind!=StandardWatchEventKinds.ENTRY_DELETE){
                        //非删除 新增job

                        File file = new File(path, pathName.toString());
                        addQuartzTask(file);

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());

            } finally {
                watchKey.reset();
            }
        }
    }

    public void close() {
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取任务配置信息
     * @param filePath
     * @return
     * @throws Exception
     */
    public ExeObj getfileinfo(File filePath) throws Exception {
        ExeObj eo =null;
        FileReader reader =null;
        try{
            Yaml yaml = new Yaml();
            reader = new FileReader(filePath);
            eo = yaml.loadAs(reader, ExeObj.class);

        }finally {
            reader.close();
        }
        return eo;

    }

    /**
     * 读取目录下所有文件
     */
    public void readAll() throws Exception{
        File file = new File(path);
        File[] array = file.listFiles();
        for (int i = 0; i < array.length; i++){
            File childFile = array[i];
            if (childFile.isFile() && !childFile.isHidden() ) {

                try {

                    addQuartzTask(childFile);


                }catch (Exception e){
                    log.error("添加任务："+childFile.getName()+"失败");
                }
            } else if (childFile.isDirectory()) {
                readAll();
            }
        }

    }

    public void addQuartzTask(File file) throws Exception {
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        if(suffix.equals("config") ){
//            System.out.println("添加Job：" + fileName);
            log.info("添加Job：" + fileName);
            //获取任务配置信息
            ExeObj map = getfileinfo(file);
            Class cls =null;
            if(map.getType()==ExeObj.TYPE_URL){
                cls=UrlJob.class;
            }else  if(map.getType()==ExeObj.TYPE_CMD) {
                cls = CmdJob.class;
            }
            if(cls !=null){
                String cron = map.getCron();
                String content = map.getContent();
                quartzTask.addJob(cls,file.getName(),Scheduler.DEFAULT_GROUP,cron,content);
            }

        }else{
//            System.out.println("非指定格式文件 添加失败" + suffix);
            log.error("非指定格式文件 添加失败" + suffix);
        }

    }

}