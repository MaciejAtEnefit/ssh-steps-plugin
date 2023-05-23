package org.jenkinsci.plugins.sshstepslegacy.util;

import com.google.common.annotations.VisibleForTesting;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.UUID;
import jenkins.security.MasterToSlaveCallable;
import org.apache.log4j.MDC;
import org.jenkinsci.plugins.sshstepslegacy.SSHServiceLegacy;
import org.jenkinsci.plugins.sshstepslegacy.steps.BasicSSHStepLegacy;

/**
 * Base Callable for all SSH Steps.
 *
 * @author Naresh Rayapati.
 */
public abstract class SSHMasterToSlaveCallable extends MasterToSlaveCallable<Object, IOException> {

  private final BasicSSHStepLegacy step;
  private final TaskListener listener;
  private SSHServiceLegacy service;

  public SSHMasterToSlaveCallable(BasicSSHStepLegacy step, TaskListener listener) {
    this.step = step;
    this.listener = listener;
  }

  @Override
  public Object call() {
    MDC.put("execution.id", UUID.randomUUID().toString());
    this.service = createService();
    return execute();
  }

  @VisibleForTesting
  public SSHServiceLegacy createService() {
    return SSHServiceLegacy
        .create(step.getRemote(), step.isFailOnError(), step.isDryRun(), listener.getLogger());
  }

  protected abstract Object execute();

  public BasicSSHStepLegacy getStep() {
    return step;
  }

  public SSHServiceLegacy getService() {
    return service;
  }
}
