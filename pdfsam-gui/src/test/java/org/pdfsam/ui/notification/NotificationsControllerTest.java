/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 22/ago/2014
 * Copyright 2013-2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.ui.notification;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.pdfsam.Pdfsam;
import org.pdfsam.PdfsamEdition;
import org.pdfsam.module.UsageService;
import org.pdfsam.test.ClearEventStudioRule;
import org.pdfsam.test.InitializeAndApplyJavaFxThreadRule;
import org.pdfsam.update.UpdateAvailableEvent;
import org.sejda.model.exception.InvalidTaskParametersException;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;

/**
 * @author Andrea Vacondio
 *
 */
public class NotificationsControllerTest {

    @ClassRule
    public static ClearEventStudioRule STUDIO_RULE = new ClearEventStudioRule();
    @Rule
    public InitializeAndApplyJavaFxThreadRule javaFxThread = new InitializeAndApplyJavaFxThreadRule();
    private UsageService service;
    private NotificationsContainer container;
    private NotificationsController victim;

    @Before
    public void setUp() {
        service = mock(UsageService.class);
        container = mock(NotificationsContainer.class);
        victim = new NotificationsController(container, service, new Pdfsam(PdfsamEdition.COMMUNITY, "name", "version"));
    }

    @Test
    public void onAddRequest() {
        AddNotificationRequestEvent event = new AddNotificationRequestEvent(NotificationType.INFO, "msg", "title");
        victim.onAddRequest(event);
        verify(container).addNotification(eq("title"), any());
    }

    @Test
    public void onRemoveRequest() {
        RemoveNotificationRequestEvent event = new RemoveNotificationRequestEvent("id");
        victim.onRemoveRequest(event);
        verify(container).removeNotification("id");
    }

    @Test
    public void onUpdateAvailable() {
        UpdateAvailableEvent event = new UpdateAvailableEvent("new version");
        victim.onUpdateAvailable(event);
        verify(container).addStickyNotification(anyString(), any());
    }

    @Test
    public void onTaskFailed() {
        TaskExecutionFailedEvent event = new TaskExecutionFailedEvent(new Exception("some exception"), null);
        victim.onTaskFailed(event);
        verify(container, never()).addNotification(anyString(), any());
    }

    @Test
    public void onInvalidParameters() {
        TaskExecutionFailedEvent event = new TaskExecutionFailedEvent(new InvalidTaskParametersException(), null);
        victim.onTaskFailed(event);
        verify(container).addNotification(anyString(), any());
    }

    @Test
    public void onTaskCompleteAndNoProDisplay() {
        when(service.getTotalUsage()).thenReturn(1L);
        TaskExecutionCompletedEvent event = new TaskExecutionCompletedEvent(1, null);
        victim.onTaskCompleted(event);
        verify(container, never()).addNotification(anyString(), any());
    }

    @Test
    public void onTaskCompleteAndProDisplay() {
        when(service.getTotalUsage()).thenReturn(5L);
        TaskExecutionCompletedEvent event = new TaskExecutionCompletedEvent(1, null);
        victim.onTaskCompleted(event);
        verify(container).addStickyNotification(anyString(), any());
    }

    @Test
    public void onTaskCompleteDontDisplayForEnterprise() {
        victim = new NotificationsController(container, service, new Pdfsam(PdfsamEdition.COMMUNITY, "name", "version"));
        when(service.getTotalUsage()).thenReturn(6L);
        TaskExecutionCompletedEvent event = new TaskExecutionCompletedEvent(1, null);
        victim.onTaskCompleted(event);
        verify(container, never()).addNotification(anyString(), any());
    }
}
