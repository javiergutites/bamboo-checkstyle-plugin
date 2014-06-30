package com.atlassian.bamboo.plugins.checkstyle.notification;

import com.atlassian.applinks.api.*;
import com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginConstants;
import com.atlassian.plugins.capabilities.api.LinkedAppWithCapabilities;
import com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.stash.codeanalysis.rest.BaseRestEntity;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.atlassian.util.concurrent.ThreadFactories.namedThreadFactory;

public class CheckstyleNotificationService implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(CheckstyleNotificationService.class);

    private static final int NO_THREADS = 3;
    private static final String CODE_ANALYSIS_CAPABILITY = "code-analysis-display";
    private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON;

    private final LinkedApplicationCapabilities linkedApplicationCapabilities;
    private final ApplicationLinkService applicationLinkService;
    private final ExecutorService executorService;

    public CheckstyleNotificationService(LinkedApplicationCapabilities linkedApplicationCapabilities,
                                         ApplicationLinkService applicationLinkService) {
        this.linkedApplicationCapabilities = linkedApplicationCapabilities;
        this.applicationLinkService = applicationLinkService;
        this.executorService = Executors.newFixedThreadPool(NO_THREADS, namedThreadFactory("codeanalysis-notifier"));
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdownNow();
    }

    public void notifyAll(@Nonnull Iterable<String> changesets) {
        for (LinkedAppWithCapabilities app : linkedApplicationCapabilities.capableOf(CODE_ANALYSIS_CAPABILITY)) {
            ApplicationLink link = getApplicationLink(app);
            if (link == null) {
                continue;
            }
            executorService.submit(new NotifyLinkedApp(app, link, changesets));
        }
    }

    private ApplicationLink getApplicationLink(LinkedAppWithCapabilities app) {
        try {
            return applicationLinkService.getApplicationLink(new ApplicationId(app.getApplicationLinkId()));
        } catch (TypeNotInstalledException e) {
            log.warn("Could not get application link for app {}. Aborting notification", app);
            return null;
        }
    }

    private static final class NotifyLinkedApp implements Runnable {

        private final Iterable<String> changesets;
        private final LinkedAppWithCapabilities app;
        private final ApplicationLink applicationLink;

        private NotifyLinkedApp(LinkedAppWithCapabilities app, ApplicationLink applicationLink,
                                Iterable<String> changesets) {
            this.app = app;
            this.changesets = changesets;
            this.applicationLink = applicationLink;
        }

        @Override
        public void run() {
            ApplicationLinkRequestFactory requestFactory = applicationLink.createNonImpersonatingAuthenticatedRequestFactory();
            String url = app.getCapabilityUrl(CODE_ANALYSIS_CAPABILITY);
            for (String changeset : changesets) {
                notify(requestFactory, url, changeset);
            }
        }

        private void notify(ApplicationLinkRequestFactory requestFactory, String url, String changeset) {
            try {
                ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.POST, url);
                request.setEntity(new Notification(changeset, getPullUrl(changeset)));
                request.setRequestContentType(CONTENT_TYPE);

                NoContentResponseHandler handler = new NoContentResponseHandler();
                request.execute(handler);

                if (!handler.isExpected()) {
                    log.warn("Unexpected response from code analysis notification receiver: {}: {}",
                            handler.status, handler.responseBody);
                }
            } catch (CredentialsRequiredException e) {
                log.warn("Credentials required for linked application {}:{}, unable to push code analysis notification",
                        new Object[] {applicationLink.getName(), applicationLink.getId(), e});
            } catch (ResponseException e) {
                log.warn("Error while sending code analysis notification to {}:{}",
                        new Object[] {applicationLink.getName(), applicationLink.getId(), e});
            }
        }

        private String getPullUrl(String changeset) {
            // TODO
            return null;
        }
    }

    private static final class NoContentResponseHandler implements ResponseHandler<Response> {

        public static final int STATUS_NO_CONTENT = javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode();

        private int status = -1;
        private String responseBody;

        @Override
        public void handle(Response response) throws ResponseException {
            status = response.getStatusCode();
            if (!isExpected()) {
                responseBody = response.getResponseBodyAsString();
            }
        }

        boolean isExpected() {
            return status == STATUS_NO_CONTENT;
        }
    }

    @JsonSerialize
    public static class Notification extends BaseRestEntity {

        // TODO use the one from code analysis rest-common

        Notification(String changeset, String url) {
            put("tool", CheckstylePluginConstants.CHECKSTYLE_TOOL);
            put("changesetId", changeset);
            put("url", url);
        }
    }
}
