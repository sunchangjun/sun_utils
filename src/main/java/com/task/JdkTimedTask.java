package com.task;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JdkTimedTask {
    static ScheduledThreadPoolExecutor stp = null;
    static int index;

    private static String getTimes() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        return format.format(date);
    }


    private static class MyTask implements Runnable {

        @Override
        public void run() {
            index++;
            System.out.println("2= " + getTimes()+" "  +index);
//            if(index >=10){
//                stp.shutdown();
//                if(stp.isShutdown()){
//                    System.out.println("停止了？？？？");
//                }
//            }
        }
    }
    public static void main(String[] args)
    {
        stp = new ScheduledThreadPoolExecutor(5);
        MyTask mytask = new MyTask();
        //mytask为线程，2是首次执行的延迟时间，最后一个参数为时间单位
//        stp.schedule(mytask, 2, TimeUnit.SECONDS);
        // 首次执行延迟2秒，之后的执行周期是1秒
//        stp.scheduleAtFixedRate(mytask, 2, 1,TimeUnit.SECONDS );
        //首次执行延迟2秒，之后从上一次任务结束到下一次任务开始时1秒
        stp.scheduleWithFixedDelay(mytask, 2, 1, TimeUnit.SECONDS);

    }

}


