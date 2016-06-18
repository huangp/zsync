package org.zanata.sync.controller;

import java.io.IOException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.zanata.sync.i18n.Messages;
import org.zanata.sync.model.JobStatus;
import org.zanata.sync.model.JobType;
import org.zanata.sync.model.SyncWorkConfig;
import org.zanata.sync.model.SyncWorkConfigBuilder;
import org.zanata.sync.service.PluginsService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("workController")
@Slf4j
@RequestScoped
public class WorkController extends HasFormController {

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private SyncWorkConfigBuilder syncWorkConfigBuilderImpl;

    @Inject
    private Messages msg;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private SyncWorkConfig syncWorkConfig;

    public SyncWorkForm getForm() {
        if(form == null) {
//            form = syncWorkConfigBuilderImpl.buildForm(getSyncWorkConfig(id));
        }
        return form;
    }

    public SyncWorkConfig getSyncWorkConfig(String id) {
        if(syncWorkConfig == null) {
//            Response response = workResourceImpl.getWork(id, "");
//            syncWorkConfig = (SyncWorkConfig)response.getEntity();
        }
        return syncWorkConfig;
    }

    public void triggerSyncToRepoJob(String id) {
//        jobResource.triggerJob(id, JobType.REPO_SYNC);
    }

    public void triggerSyncToServerJob(String id) {
//        jobResource.triggerJob(id, JobType.SERVER_SYNC);
    }

    public boolean isSyncToRepoRunning() {
        return isJobRunning(JobType.REPO_SYNC);
    }

    public JobStatus getRepoSyncStatus(String id) {
//        Response response = jobResource.getJobStatus(id, JobType.REPO_SYNC);
//        return (JobStatus)response.getEntity();
        return null;
    }

    public JobStatus getServerSyncStatus(String id) {
//        Response response = jobResource.getJobStatus(id, JobType.SERVER_SYNC);
//        return (JobStatus)response.getEntity();
        return null;
    }

    public boolean isSyncToServerRunning() {
        return isJobRunning(JobType.SERVER_SYNC);
    }

    public void deleteWork(String id) throws IOException {
//        workResourceImpl.deleteWork(id);
//        FacesContext.getCurrentInstance().getExternalContext()
//            .redirect("/home.jsf");
    }

    public void cancelRunningJob(String jobType) {
//        jobResource.cancelRunningJob(id, JobType.valueOf(jobType));
    }

    private boolean isJobRunning(JobType jobType) {
//        Response response =
//                jobResource.getJob(id, jobType, JobStatusType.RUNNING);
//        List<JobSummary> result = (List<JobSummary>) response.getEntity();
//        return !result.isEmpty();
        return false;
    }

    @Override
    protected Messages getMessage() {
        return msg;
    }

    @Override
    protected PluginsService getPluginService() {
        return pluginsServiceImpl;
    }

    @Override
    public String onSubmit() throws IOException {
//        Response response = workResourceImpl.updateWork(form);
//        errors = (Map<String, String>) response.getEntity();
        if (!errors.isEmpty()) {
            return null;
        }
//        FacesContext.getCurrentInstance().getExternalContext()
//            .redirect("/home.jsf");
        return "";
    }
}