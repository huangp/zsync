package org.zanata.sync.api.impl;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.sync.api.WorkResource;
import org.zanata.sync.common.plugin.RepoExecutor;
import org.zanata.sync.controller.SyncWorkForm;
import org.zanata.sync.exception.WorkNotFoundException;
import org.zanata.sync.model.SyncWorkConfig;
import org.zanata.sync.model.SyncWorkConfigBuilder;
import org.zanata.sync.service.PluginsService;
import org.zanata.sync.service.SchedulerService;
import org.zanata.sync.service.WorkService;
import org.zanata.sync.validation.SyncWorkFormValidator;
import com.google.common.collect.Maps;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RequestScoped
@Path("/work")
@Produces("application/json")
@Consumes("application/json")
public class WorkResourceImpl {
    private static final Logger log =
            LoggerFactory.getLogger(WorkResourceImpl.class);

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Inject
    private WorkService workServiceImpl;

    @Inject
    private SyncWorkFormValidator formValidator;

    @Inject
    private SyncWorkConfigBuilder syncWorkConfigBuilder;

    @Inject
    private PluginsService pluginsService;

    @GET
    @Path("supported")
    public Response getSupportedSourceRepoPlugin() {
        List<RepoExecutor> srcRepoPlugins =
                pluginsService.getAvailableSourceRepoPlugins();
        List<Map<String, Object>> plugins =
                srcRepoPlugins.stream().map(plugin -> {
                    // TODO maybe use a DTO for json serialization
                    Map<String, Object> pluginMap = Maps.newHashMap();
                    pluginMap.put("name", plugin.getName());
                    pluginMap.put("description", plugin.getDescription());
                    pluginMap.put("fields", plugin.getFields());
                    return pluginMap;
                }).collect(Collectors.toList());
        return Response.ok(plugins).build();
    }

    @GET
    public Response
            getWork(@QueryParam(value = "id") @DefaultValue("") String id,
                    @QueryParam(value = "type") @DefaultValue("") String type) {
        if (StringUtils.isEmpty(id)) {
            return getAllWork(type);
        } else {
            try {
                if(!type.equals("summary")) {
                    return Response.ok(schedulerServiceImpl.getWork(id)).build();
                } else {
                    return Response.ok(schedulerServiceImpl.getWorkSummary(id)).build();
                }
            } catch (WorkNotFoundException e) {
                log.error("fail getting all jobs", e);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
    }

    @POST
    public Response createWork(SyncWorkForm form) {
        Map<String, String> errors = formValidator.validateJobForm(form);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        SyncWorkConfig syncWorkConfig = syncWorkConfigBuilder.buildObject(form);
        // TODO pahuang here we should persist the refresh token
        try {
            workServiceImpl.updateOrPersist(syncWorkConfig);
            schedulerServiceImpl.scheduleWork(syncWorkConfig);
        } catch (SchedulerException e) {
            log.error("Error trying to schedule job", e);
            errors.put("error", e.getMessage());
            return Response.serverError().entity(errors).build();
        }
        // TODO create URI
        return Response.created(URI.create("")).entity(errors).build();
    }

    @PUT
    public Response updateWork(SyncWorkForm form) {
        if(form.getId() == null) {
            return createWork(form);
        }
        Map<String, String> errors = formValidator.validateJobForm(form);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        SyncWorkConfig syncWorkConfig = syncWorkConfigBuilder.buildObject(form);

        try {
            workServiceImpl.updateOrPersist(syncWorkConfig);
            schedulerServiceImpl.rescheduleWork(syncWorkConfig);
        } catch (SchedulerException e) {
            log.error("Error rescheduling work", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errors).build();
        }
        // TODO create URI
        return Response.created(URI.create("")).entity(errors).build();
    }

    @DELETE
    public Response deleteWork(String id) {
        try {
            workServiceImpl.deleteWork(new Long(id));
        } catch (WorkNotFoundException e) {
            log.error("No work found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    private Response getAllWork(String type) {
        try {
            if(!type.equals("summary")) {
                return Response.ok(schedulerServiceImpl.getAllWork()).build();
            } else {
                return Response.ok(schedulerServiceImpl.getAllWorkSummary()).build();
            }
        } catch (SchedulerException e) {
            log.error("fail getting all jobs", e);
            return Response.serverError().build();
        }
    }
}
