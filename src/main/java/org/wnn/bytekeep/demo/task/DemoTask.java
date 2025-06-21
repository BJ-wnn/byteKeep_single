package org.wnn.bytekeep.demo.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author NanNan Wang
 */

//@Component
public class DemoTask {

    // 每隔5秒执行一次
    @Scheduled(fixedRate = 5000)
    public void scheduledTask() {
        System.out.println("定时任务执行：" + LocalDateTime.now());
    }

    // 每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyTask() {
        System.out.println("每天凌晨1点执行任务");
    }


}
