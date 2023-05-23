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
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Step to execute a command on remote node.
 *
 * @author Naresh Rayapati
 */
public class CommandStepLegacy extends BasicSSHStepLegacy {

  private static final long serialVersionUID = 1000_0001_582L;

  @Getter
  private final String command;

  @Getter
  @DataBoundSetter
  private boolean sudo = false;

  @DataBoundConstructor
  public CommandStepLegacy(String command) {
    this.command = command;
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(this, context);
  }

  @Extension
  public static class DescriptorImpl extends SSHStepDescriptorLegacyImpl {

    @Override
    public String getFunctionName() {
      return "legacy_sshCommand";
    }

    @Override
    public String getDisplayName() {
      return getPrefix() + getFunctionName() + " - Execute command on remote node.";
    }
  }

  public static class Execution extends SSHStepExecutionLegacy {

    private static final long serialVersionUID = -5293952534324828128L;

    protected Execution(CommandStepLegacy step, StepContext context)
        throws IOException, InterruptedException {
      super(step, context);
    }

    @Override
    protected Object run() throws Exception {
      CommandStepLegacy step = (CommandStepLegacy) getStep();
      if (Util.fixEmpty(step.getCommand()) == null) {
        throw new IllegalArgumentException("command is null or empty");
      }

      return getChannel().call(new CommandCallable(step, getListener()));
    }

    private static class CommandCallable extends SSHMasterToSlaveCallable {

      public CommandCallable(CommandStepLegacy step, TaskListener listener) {
        super(step, listener);
      }

      @Override
      public Object execute() {
        CommandStepLegacy step = (CommandStepLegacy) getStep();
        return getService().executeCommand(step.getCommand(), step.isSudo());
      }
    }
  }
}
