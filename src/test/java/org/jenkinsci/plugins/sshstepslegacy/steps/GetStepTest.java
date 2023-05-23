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
 * Unit test cases for GetStep class.
 *
 * @author Naresh Rayapati
 */
public class GetStepTest extends BaseTest {

  final String path = "test.sh";
  final String filterBy = "name";
  final String filterRegex = null;

  @Mock
  FilePath filePathMock;

  GetStepLegacy.Execution stepExecution;

  @Before
  public void setup() throws IOException, InterruptedException {

    when(filePathMock.child(any())).thenReturn(filePathMock);
    when(filePathMock.exists()).thenReturn(true);
    when(filePathMock.isDirectory()).thenReturn(false);
    when(filePathMock.getRemote()).thenReturn(path);

    when(contextMock.get(FilePath.class)).thenReturn(filePathMock);

  }

  @Test
  public void testWithEmptyFromThrowsIllegalArgumentException() throws Exception {
    final GetStepLegacy step = new GetStepLegacy("", path);
    stepExecution = new GetStepLegacy.Execution(step, contextMock);

    // Execute and assert Test.
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> stepExecution.run())
        .withMessage("from is null or empty")
        .withStackTraceContaining("IllegalArgumentException")
        .withNoCause();
  }

  @Test
  public void testWithEmptyIntoThrowsIllegalArgumentException() throws Exception {
    final GetStepLegacy step = new GetStepLegacy(path, "");
    step.setOverride(true);
    stepExecution = new GetStepLegacy.Execution(step, contextMock);

    // Execute and assert Test.
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> stepExecution.run())
        .withMessage("into is null or empty")
        .withStackTraceContaining("IllegalArgumentException")
        .withNoCause();
  }

  @Test
  public void testSuccessfulExecuteScript() throws Exception {
    final GetStepLegacy step = new GetStepLegacy(path, path);
    step.setOverride(true);

    // Since SSHService is a mock, it is not validating remote.
    stepExecution = new GetStepLegacy.Execution(step, contextMock);

    // Execute Test.
    stepExecution.run();

    // Assert Test
    verify(sshServiceMock, times(1)).get(path, path, filterBy, filterRegex);
  }
}
