package com.cs.crond.watcher.entity;

import lombok.Data;

@Data
public class ExeObj  {
    public static final int TYPE_URL=1;//urlJob
    public static final int TYPE_CMD=2;//cmdJob
    private String content;//执行内容
    private String cron;//定时器表达式
    private int type;//任务类型
}
