package com.ailk.jt.task.Scheduler;

/*import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ailk.jt.util.PropertiesUtil;*/

@Deprecated
public class CalculateAllScheduler {
	/*private static final Logger log = Logger.getLogger(CalculateAllScheduler.class);// 获取日志打印对象

	public static void main(String[] args) {
		CalculateAllScheduler ccs = new CalculateAllScheduler();
		try {
			ccs.run();
		} catch (Exception e) {
		}
	}

	private void run() throws Exception {
		log.error("config scheduler start..........");
		// 1、创建一个任务
		JobDetail CalculateChangeJob = JobBuilder.newJob(CalculateAllJob.class).build();
		// 2、创建一个触发器
		String updateJTSourceCron = PropertiesUtil.getValue("updateJTSourceCron");
		CronTrigger updateJTSourceTrigger = TriggerBuilder.newTrigger().withIdentity("smsTrigger", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule(updateJTSourceCron)).build();
		// 3、创建一个任务日程表
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		// 4、将任务和触发器放入任务日程表
		scheduler.scheduleJob(CalculateChangeJob, updateJTSourceTrigger);
		log.error("config scheduler end..........");
		// 5、执行任务日程表
		log.error("scheduler start..........");
		scheduler.start();
		try {
			String updateJTSourceCronTime = PropertiesUtil.getValue("updateJTSourceCronTime");
			Thread.sleep(Long.valueOf(updateJTSourceCronTime));
		} catch (Exception e) {
		}
	}*/
}
