package org.jenkinsci.plugins.sshstepslegacy.steps;

import hudson.Extension;
import hudson.Util;
import hudson.model.TaskListener;
import java.io.IOException;
import lombok.Getter;
import org.jenkinsci.plugins.sshstepslegacy.util.SSHMasterToSlaveCallable;
import org.jenkinsci.plugins.sshstepslegacy.util.SSHStepDescriptorLegacyImpl;
import org.jenkinsci.plugins.sshstepslegacy.util.SSHStepExecutionLegacy;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Step to remove a file/directory on remote node.
 *
 * @author Naresh Rayapati
 */
public class RemoveStepLegacy extends BasicSSHStepLegacy {

  private static final long serialVersionUID = 1000_0001_255L;

  @Getter
  private final String path;

  @DataBoundConstructor
  public RemoveStepLegacy(String path) {
    this.path = path;
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(this, context);
  }

  @Extension
  public static class DescriptorImpl extends SSHStepDescriptorLegacyImpl {

    @Override
    public String getFunctionName() {
      return "legacy_sshRemove";
    }

    @Override
    public String getDisplayName() {
      return getPrefix() + getFunctionName() + " - Remove a file/directory from remote node.";
    }
  }

  public static class Execution extends SSHStepExecutionLegacy {

    private static final long serialVersionUID = 862708152481251266L;

    protected Execution(RemoveStepLegacy step, StepContext context)
        throws IOException, InterruptedException {
      super(step, context);
    }

    @Override
    protected Object run() throws Exception {
      RemoveStepLegacy step = (RemoveStepLegacy) getStep();
      if (Util.fixEmpty(step.getPath()) == null) {
        throw new IllegalArgumentException("path is null or empty");
      }

      return getChannel().call(new RemoveCallable(step, getListener()));
    }

    private static class RemoveCallable extends SSHMasterToSlaveCallable {

      public RemoveCallable(RemoveStepLegacy step, TaskListener listener) {
        super(step, listener);
      }

      @Override
      public Object execute() {
        return getService().remove(((RemoveStepLegacy) getStep()).getPath());
      }
    }
  }
}
