package org.jenkinsci.plugins.sshstepslegacy.steps;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hudson.FilePath;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit test cases for ScriptStep class.
 *
 * @author Naresh Rayapati
 */
public class ScriptStepTest extends BaseTest {

  final String scriptName = "test.sh";

  @Mock
  FilePath filePathMock;

  ScriptStepLegacy.Execution stepExecution;

  @Before
  public void setup() throws IOException, InterruptedException {

    when(filePathMock.child(any())).thenReturn(filePathMock);
    when(filePathMock.exists()).thenReturn(true);
    when(filePathMock.isDirectory()).thenReturn(false);
    when(filePathMock.getRemote()).thenReturn(scriptName);

    when(contextMock.get(FilePath.class)).thenReturn(filePathMock);

  }

  @Test
  public void testWithEmptyCommandThrowsIllegalArgumentException() throws Exception {
    final ScriptStepLegacy step = new ScriptStepLegacy("");
    stepExecution = new ScriptStepLegacy.Execution(step, contextMock);

    // Execute and assert Test.
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> stepExecution.run())
        .withMessage("script is null or empty")
        .withStackTraceContaining("IllegalArgumentException")
        .withNoCause();
  }

  @Test
  public void testSuccessfulExecuteScript() throws Exception {
    final ScriptStepLegacy step = new ScriptStepLegacy(scriptName);

    // Since SSHService is a mock, it is not validating remote.
    stepExecution = new ScriptStepLegacy.Execution(step, contextMock);

    // Execute Test.
    stepExecution.run();

    // Assert Test
    verify(sshServiceMock, times(1)).executeScriptFromFile(scriptName);
  }
}
