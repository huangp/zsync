/*
 * Copyright 2016, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.sync.quartz;

import javax.enterprise.context.Dependent;

import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.sync.events.JobProgressEvent;
import org.zanata.sync.events.JobRunCompletedEvent;
import org.zanata.sync.model.JobStatusType;
import org.zanata.sync.model.JobType;
import org.zanata.sync.model.SyncWorkConfig;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
// NOTE: if I use ApplicationScope it will fail (on second time onwards) with WELD-001303: No active contexts for scope type javax.enterprise.context.ApplicationScoped
// Although it's dependent scope but it's used by an application scoped bean so there should only be one instance
@Dependent
public class SyncJobListener implements JobListener {
    private static final Logger log =
            LoggerFactory.getLogger(SyncJobListener.class);
    @Override
    public String getName() {
        return "Sync Job Listener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("job about to be fired: {}", context.getTrigger());
        SyncJobDataMap syncJobDataMap = SyncJobDataMap.fromContext(context);
        SyncWorkConfig workConfig = syncJobDataMap.getWorkConfig();
        JobType jobType = syncJobDataMap.getJobType();

        JobProgressEvent event = JobProgressEvent
                .running(context.getFireInstanceId(), workConfig, jobType);
        fireCDIEvent(event);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context,
            JobExecutionException jobException) {
        log.info("job fired with trigger: {}", context.getTrigger());
        JobRunCompletedEvent event;
        SyncJobDataMap syncJobDataMap = SyncJobDataMap.fromContext(context);
        SyncWorkConfig workConfig = syncJobDataMap.getWorkConfig();
        JobType jobType = syncJobDataMap.getJobType();
        if (jobException != null) {
            event = new JobRunCompletedEvent(context.getFireInstanceId(),
                    workConfig.getId(),
                    context.getJobRunTime(),
                    context.getFireTime(),
                    jobType, JobStatusType.ERROR);
        } else {
            event = new JobRunCompletedEvent(context.getFireInstanceId(),
                    workConfig.getId(),
                    context.getJobRunTime(),
                    context.getFireTime(),
                    jobType, JobStatusType.COMPLETE);
        }
        fireCDIEvent(event);
    }

    private static void fireCDIEvent(Object event) {
        BeanManagerProvider.getInstance().getBeanManager()
                .fireEvent(event);
    }
}
