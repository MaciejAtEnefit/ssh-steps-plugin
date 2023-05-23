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
 * Unit test cases for PutStep class.
 *
 * @author Naresh Rayapati
 */
public class PutStepTest extends BaseTest {

  final String path = "test.sh";
  final String filterBy = "name";
  final String filterRegex = null;

  @Mock
  FilePath filePathMock;

  PutStepLegacy.Execution stepExecution;

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
    final PutStepLegacy step = new PutStepLegacy("", path);
    stepExecution = new PutStepLegacy.Execution(step, contextMock);

    // Execute and assert Test.
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> stepExecution.run())
        .withMessage("from is null or empty")
        .withStackTraceContaining("IllegalArgumentException")
        .withNoCause();
  }

  @Test
  public void testWithEmptyIntoThrowsIllegalArgumentException() throws Exception {
    final PutStepLegacy step = new PutStepLegacy(path, "");
    stepExecution = new PutStepLegacy.Execution(step, contextMock);

    // Execute and assert Test.
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> stepExecution.run())
        .withMessage("into is null or empty")
        .withStackTraceContaining("IllegalArgumentException")
        .withNoCause();
  }

  @Test
  public void testSuccessfulPut() throws Exception {
    final PutStepLegacy step = new PutStepLegacy(path, path);

    // Since SSHService is a mock, it is not validating remote.
    stepExecution = new PutStepLegacy.Execution(step, contextMock);

    // Execute Test.
    stepExecution.run();

    // Assert Test
    verify(sshServiceMock, times(1)).put(path, path, filterBy, filterRegex);
  }
}
