package org.jenkinsci.plugins.sshstepslegacy.steps;

import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.TaskListener;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.jenkinsci.plugins.sshstepslegacy.util.SSHMasterToSlaveCallable;
import org.jenkinsci.plugins.sshstepslegacy.util.SSHStepDescriptorLegacyImpl;
import org.jenkinsci.plugins.sshstepslegacy.util.SSHStepExecutionLegacy;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Step to place a file/directory onto a remote node.
 *
 * @author Naresh Rayapati
 */
public class PutStepLegacy extends BasicSSHStepLegacy {

  private static final long serialVersionUID = 1000_0001_149L;

  @Getter
  private final String from;

  @Getter
  private final String into;

  @Getter
  @Setter
  @DataBoundSetter
  private String filterBy = "name";

  @Getter
  @Setter
  @DataBoundSetter
  private String filterRegex;

  @DataBoundConstructor
  public PutStepLegacy(String from, String into) {
    this.from = from;
    this.into = into;
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(this, context);
  }

  @Extension
  public static class DescriptorImpl extends SSHStepDescriptorLegacyImpl {

    @Override
    public String getFunctionName() {
      return "legacy_sshPut";
    }

    @Override
    public String getDisplayName() {
      return getPrefix() + getFunctionName() + " - Put a file/directory on remote node.";
    }
  }

  public static class Execution extends SSHStepExecutionLegacy {

    private static final long serialVersionUID = -4497192469254138827L;

    protected Execution(PutStepLegacy step, StepContext context)
        throws IOException, InterruptedException {
      super(step, context);
    }

    @Override
    protected Object run() throws Exception {
      PutStepLegacy step = (PutStepLegacy) getStep();
      FilePath ws = getContext().get(FilePath.class);
      assert ws != null;
      FilePath fromPath;

      if (Util.fixEmpty(step.getFrom()) == null) {
        throw new IllegalArgumentException("from is null or empty");
      }

      fromPath = ws.child(step.getFrom());

      if (!fromPath.exists()) {
        throw new IllegalArgumentException(fromPath.getRemote() + " does not exist.");
      }

      if (Util.fixEmpty(step.getInto()) == null) {
        throw new IllegalArgumentException("into is null or empty");
      }

      return getChannel().call(new PutCallable(step, getListener(), fromPath.getRemote()));
    }

    private static class PutCallable extends SSHMasterToSlaveCallable {

      private final String from;

      public PutCallable(PutStepLegacy step, TaskListener listener, String from) {
        super(step, listener);
        this.from = from;
      }

      @Override
      public Object execute() {
        final PutStepLegacy step = (PutStepLegacy) getStep();
        return getService().put(from, step.getInto(), step.getFilterBy(), step.getFilterRegex());
      }
    }
  }
}
