package viettel.com.sf;

import viettel.com.sf.utils.config.Config;
import viettel.com.sf.utils.db.DataSource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class MainAgent {
	private static Scheduler scheduler;

	static {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
		} catch (SchedulerException e) {
			log.error("Cannot create scheduler", e);
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		try {
			val ini = Config.load("./config/config.ini");
			DataSource.setConfig(ini);

			val s = ini.get("GENERAL", "tasks");
			if (!s.isPresent()) {
				log.info("No task was registered!");
				return;
			}

			val tasks = s.get().split("\\s*,\\s*");
			for (val taskName : tasks) {
				ini.get(taskName).ifPresent(task -> register(taskName, task));
			}
			scheduler.start();
		} catch (IOException | SchedulerException e) {
			log.error("Error:", e);
		}
	}

	private static void register(String taskName, Map<String, String> task) {
		try {
			log.info("Registering task: {}", task);

			val jobName = task.get("task");
			val jobClass = Class.forName(jobName);

			val job = JobBuilder.newJob(jobClass.asSubclass(Job.class))
				.withIdentity(jobName, taskName)
				.build();
			log.info("Job detail: {}", job);

			val trigger = TriggerBuilder.newTrigger()
				.withIdentity(jobName + "Trigger", taskName)
				.withSchedule(CronScheduleBuilder.cronSchedule(task.get("cron")))
				.build();
			scheduler.scheduleJob(job, trigger);
		} catch (ClassNotFoundException | SchedulerException | ClassCastException e) {
			log.error("Error", e);
		}
	}
}
