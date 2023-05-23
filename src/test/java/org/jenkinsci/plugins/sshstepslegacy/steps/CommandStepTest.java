package org.jenkinsci.plugins.sshstepslegacy.steps;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

/**
 * Unit test cases for CommandStep class.
 *
 * @author Naresh Rayapati
 */
public class CommandStepTest extends BaseTest {

  CommandStepLegacy.Execution stepExecution;

  @Test
  public void testWithEmptyCommandThrowsIllegalArgumentException() throws Exception {
    final CommandStepLegacy step = new CommandStepLegacy("");
    stepExecution = new CommandStepLegacy.Execution(step, contextMock);

    // Execute and assert Test.
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> stepExecution.run())
        .withMessage("command is null or empty")
        .withStackTraceContaining("IllegalArgumentException")
        .withNoCause();
  }

  @Test
  public void testSuccessfulExecuteCommand() throws Exception {
    final CommandStepLegacy step = new CommandStepLegacy("ls -lrt");

    // Since SSHService is a mock, it is not validating remote.
    stepExecution = new CommandStepLegacy.Execution(step, contextMock);

    // Execute Test.
    stepExecution.run();

    // Assert Test
    verify(sshServiceMock, times(1)).executeCommand("ls -lrt", false);
  }
}
