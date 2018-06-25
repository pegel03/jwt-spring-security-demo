package org.zerhusen.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.*;
import org.zerhusen.job.SampleJob;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class QuartzConfig {

    @Bean
    public SpringBeanJobFactory jobFactory() {
//        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        SpringBeanJobFactory springBeanJobFactory = new SpringBeanJobFactory();
        SchedulerContext schedulerContext = new SchedulerContext();
        springBeanJobFactory.setSchedulerContext(schedulerContext);
        return springBeanJobFactory;
//        SpringBeanAutowiringSupport
    }

    @Bean(name = "myScheduler")
    public Scheduler schedulerFactory(DataSource dataSource, SpringBeanJobFactory jobFactory
        , @Qualifier("sampleJobTrigger") Trigger sampleJobTrigger) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
//        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
//        try {
//            stdSchedulerFactory.initialize();
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//        schedulerFactoryBean.setSchedulerFactory(stdSchedulerFactory);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        try {
            schedulerFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        schedulerFactoryBean.setTriggers(sampleJobTrigger((JobDetail) sampleJobDetail().getJobDataMap().get("sampleJobDetail"), 4000));
//        schedulerFactoryBean.setAutoStartup(true);
//        schedulerFactoryBean.start();
//        return schedulerFactoryBean;
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            scheduler.setJobFactory(jobFactory);
//            scheduler.scheduleJob(sampleJobTrigger((JobDetail) sampleJobDetail(), 3000));
            scheduler.scheduleJob((JobDetail) sampleJobTrigger.getJobDataMap().get("jobDetail"), sampleJobTrigger);
            scheduler.start();
            System.out.println("pgl after scheduler start dusss");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        Properties props = null;
        try {
            propertiesFactoryBean.afterPropertiesSet();
            props = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    @Bean
    public JobDetailFactoryBean sampleJobDetail() {

        return createJobDetail(SampleJob.class);
    }

    @Bean(name = "sampleJobTrigger")
    public SimpleTrigger sampleJobTrigger(@Qualifier("sampleJobDetail") JobDetail jobDetail,
                                          @Value("${samplejob.frequency}") long frequency) {
        SimpleTriggerFactoryBean ff = createTrigger(jobDetail, frequency);
        SimpleTrigger simpleTrigger = ff.getObject();
        return simpleTrigger;
    }

    private static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable to be stored in DB:
        factoryBean.setDurability(true);
        return factoryBean;
    }

    private static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        // in case of misfire, ignore all missed triggers and continue :
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return factoryBean;
    }

    // Use this method for creating cron triggers instead of simple triggers:
    private static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }

}
