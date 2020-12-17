package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.ArtifactDownloaderTask;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.DownloadItem;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.LinkedList;
import java.util.List;

public abstract class BambooStages {

    protected static final Requirement DockerRequirement = new Requirement("system.docker.executable");

    protected List<Task> getDefaultTaskList() {
        List<Task> tasks = new LinkedList<>();
        tasks.add(CheckoutTaskDefaultRepository);
        tasks.add(PlanTask.getEnvironmentScriptTask());
        return tasks;
    }

    protected List<Task> getMavenTaskList() {
        List<Task> tasks = getDefaultTaskList();
        tasks.add(PlanTask.getMavenEnvironmentScriptTask());
        tasks.add(PlanTask.getMavenTestScriptTask());
        tasks.add(PlanTask.getMavenBuildScriptTask());
        return tasks;
    }

    protected List<Task> getLibMavenTaskList() {
        List<Task> tasks = getMavenTaskList();
        tasks.add(PlanTask.getJetpackSaasTriggerTask());
        tasks.add(PlanTask.getJetpackSaasBreakBuildTask());
        tasks.add(PlanTask.getgetReleaseInfoTask());
        tasks.add(PlanTask.getInjectVariablesTask());
        tasks.add(PlanTask.getDeployLibScriptTask());
        tasks.add(PlanTask.getGitTagTask());
        tasks.add(PlanTask.getGitCommitTask());
        return tasks;
    }

    protected List<Task> addJetPackSaasTaskList(List<Task> tasks) {
        tasks.add(PlanTask.getJetpackSaasTriggerTask());
        tasks.add(PlanTask.getJetpackSaasBreakBuildTask());
        tasks.add(PlanTask.getInjectVariablesTask());
        tasks.add(PlanTask.getMavenReleaseScriptTask());
        tasks.add(PlanTask.getGitTagTask());
        tasks.add(PlanTask.getGitCommitTask());
        return tasks;
    }

    protected List<Task> getDeployTaskList() {
        final List<Task> tasks = new LinkedList<>();
        tasks.add(getCleanWorkingDirectoryTask());
        return tasks;
    }

    protected Task getCleanWorkingDirectoryTask() {
        return new CleanWorkingDirectoryTask()
                .description("Clean working directory");
    }

    protected Task getArtifactDownloaderTask() {
        return new ArtifactDownloaderTask()
                .description("Download release contents")
                .artifacts(new DownloadItem().allArtifacts(true));
    }

    protected Task getArtifactDownloaderTask(final String projectKey, final String masterPlanKey) {
        return new ArtifactDownloaderTask()
                .description("Mast Plan")
                .sourcePlan(new PlanIdentifier(projectKey, masterPlanKey))
                .artifacts(new DownloadItem().allArtifacts(true));
    }

    protected final VcsCheckoutTask CheckoutTaskDefaultRepository = new VcsCheckoutTask()
            .description("Checkout Default Repository")
            .checkoutItems(new CheckoutItem().defaultRepository());

    public static final Stage getArtifactDownloadStage(final String projectKey, final String masterPlanKey) {
        return new ArtifactDownloadStage().getStage(projectKey, masterPlanKey);
    }

    public static final Stage getCoverityStage() {
        return new CoverityStage().getStage();
    }

    public static final Stage getAuqaSecScanStage() {
        return new AquaSecScanStage().getStage();
    }

    public static final Stage getLibBuildStage() {
        return new LibBuildStage().getStage();
    }

    public static final Stage getBuildStage() {
        return BambooStages.getBuildStage(null);
    }

    public static final Stage getBuildStage(final String filePath) {
        return new BuildStage().getStage(filePath);
    }

    public static final Stage getDeployStage(final String env, final boolean isManual) {
        return BambooStages.getDeployStage(env, isManual, null, null);
    }

    public static final Stage getDeployStage(final String env, final boolean isManual, final String filePath) {
        return BambooStages.getDeployStage(env, isManual, filePath, null);
    }

    public static final Stage getDeployStage(final String env, final boolean isManual,
                                             final String filePath, final String repositoryName) {
        return new DeployStage().getStage(env, isManual, filePath, repositoryName);
    }

    public static final Task[] getDeploymentTasks(final String env, final String filePath, final String repositoryName) {
        return new DeployStage().getTasks(env, filePath, repositoryName);
    }

    public static final Stage getSVTStage(final List<String> testTypes, final boolean isManual, final String repositoryName) {
        return new SVTCICDStage().getStage(testTypes, isManual, repositoryName);
    }

    public static final Task[] getSVTCICDTasks(final List<String> testTypes, final String repositoryName) {
        return new SVTCICDStage().getTasks(testTypes, repositoryName);
    }

    public static final Stage getCRQiTAMStage(final boolean isManual, final String repositoryName) {
        return new CRQiTAMStage().getStage(isManual, repositoryName);
    }

    public static final Task[] getCRQiTAMTasks(final String repositoryName) {
        return new CRQiTAMStage().getTasks(repositoryName);
    }
}