package org.jenkinsci.plugins.sshstepslegacy.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.IOException;
import java.io.PrintStream;
import org.jenkinsci.plugins.sshstepslegacy.SSHServiceLegacy;
import org.jenkinsci.plugins.sshstepslegacy.util.TestVirtualChannel;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Base Test Class.
 *
 * @author Naresh Rayapati
 */
public class BaseTest {

  @Mock
  TaskListener taskListenerMock;
  @Mock
  Run<?, ?> runMock;
  @Mock
  EnvVars envVarsMock;
  @Mock
  PrintStream printStreamMock;
  @Mock
  SSHServiceLegacy sshServiceMock;
  @Mock
  StepContext contextMock;
  @Mock
  Launcher launcherMock;

  private AutoCloseable closeable;
  private MockedStatic<SSHServiceLegacy> sshService;

  @Before
  public void setUpBase() throws IOException, InterruptedException {

    closeable = MockitoAnnotations.openMocks(this);

    when(runMock.getCauses()).thenReturn(null);
    when(taskListenerMock.getLogger()).thenReturn(printStreamMock);
    doNothing().when(printStreamMock).println();
    when(launcherMock.getChannel()).thenReturn(new TestVirtualChannel());

    sshService = Mockito.mockStatic(SSHServiceLegacy.class);
    sshService.when(() -> SSHServiceLegacy.create(any(), anyBoolean(), anyBoolean(), any())).thenReturn(sshServiceMock);

    when(contextMock.get(Run.class)).thenReturn(runMock);
    when(contextMock.get(TaskListener.class)).thenReturn(taskListenerMock);
    when(contextMock.get(EnvVars.class)).thenReturn(envVarsMock);
    when(contextMock.get(Launcher.class)).thenReturn(launcherMock);
  }

  @After
  public void tearUpBase() throws Exception {
    sshService.close();
    closeable.close();
  }
}
