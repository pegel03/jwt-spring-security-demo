package org.zerhusen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.zerhusen.config.QuartzConfig;
import org.zerhusen.job.SampleJob;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

//@SpringApplicationConfiguration(classes = Application.class)

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = QuartzConfig.class)
public class QuartzApplicationTest { // extends AbstractTransactionalTestNGSpringContextTests {


//import com.kaviddiss.bootquartz.job.SampleJob;
//import org.quartz.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
//import org.testng.annotations.Test;

    @Autowired
//    @Qualifier("myScheduler")
    private Scheduler scheduler;

    @Test
    public void test() throws Exception {

        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
            .storeDurably(true)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(simpleSchedule().withIntervalInSeconds(2).repeatForever())
            .startNow()
            .build();

        scheduler.scheduleJob(jobDetail, trigger);

        Thread.sleep(8000);
    }
}
