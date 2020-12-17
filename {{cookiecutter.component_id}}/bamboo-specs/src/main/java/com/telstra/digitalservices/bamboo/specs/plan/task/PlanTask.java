package com.telstra.digitalservices.bamboo.specs.plan.task;

import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import org.apache.commons.lang3.StringUtils;

abstract public class PlanTask {

    private static final String BAMBOO_BUILD_DIR = "/var/home/ci/bamboo_build";
    protected static final String DOCKER_REGIDTRY = "docker-registry-v2.ae.sda.corp.telstra.com";

    protected static final String M2_GROUP = "/.m2/";
    protected static final String SCRIPT_GROUP = "/scripts/";
    protected static final String JETPACK_SCRIPT_GROUP = SCRIPT_GROUP + "jetpack/";
    protected static final String LOCAL_FILE_GROUP = "./";

    public static final Task getEnvironmentScriptTask() {
        return new SettingScriptTask().getEnvironment();
    }

    public static final Task getMasterSharedImageTask() {
        return new SettingScriptTask().getMasterSharedImage();
    }

    public static final Task getMavenEnvironmentScriptTask() {
        return new SettingScriptTask().getMavenEnvironment();
    }

    public static final Task getCoverityScriptTask() {
        return new SettingScriptTask().getCoverity();
    }

    public static final Task getAquaSecScanScriptTask() {
        return new SettingScriptTask().getAquaSecScan();
    }

    public static final Task getVariablesScriptTask() {
        return new SettingScriptTask().getVariables();
    }

    public static final Task getDockerImageScriptTask(final String filePath) {
        return new SettingScriptTask().getDockerImage(filePath);
    }

    public static final boolean isLocalFile(final String filePath) {
        return filePath.startsWith(LOCAL_FILE_GROUP);
    }

    public static final Task getDeployCheckoutTask(final String repositoryName) {
        return new SettingScriptTask().getDeployCheckout(repositoryName);
    }

    public static final Task getDeployTask(final String env, final String filePath) {
        return new SettingScriptTask().getDeploy(env, filePath);
    }

    public static final Task getSVTCICDTask(final String testType) {
        return new SettingScriptTask().getSVTCICD(testType);
    }

    public static final Task getCRQiTAMTask() {
        return new SettingScriptTask().getCRQiTAMS();
    }

    public static final Task getInjectVariablesTask() {
        return new SettingScriptTask().getInjectVariables();
    }

    public static final Task getInjectMasterVariablesTask() {
        return new SettingScriptTask().getInjectMasterVariables();
    }

    public static final Task getMavenTestScriptTask() {
        return new MavenScriptTask().getMavenTest();
    }

    public static final Task getMavenBuildScriptTask() {
        return new MavenScriptTask().getMavenBuild();
    }

    public static final Task getMavenReleaseScriptTask() {
        return new MavenScriptTask().getMavenRelease();
    }

    public static final Task getGitCommitTask() {
        return new SettingScriptTask().getGitCommit();
    }

    public static final Task getGitTagTask() {
        return new SettingScriptTask().getGitTag();
    }

    public static final Task getGitPushTask() {
        return new SettingScriptTask().getGitPush();
    }


    public static final Task getDeployLibScriptTask() {
        return new MavenScriptTask().getDeployLib();
    }

    public static final Task getgetReleaseInfoTask() {
        return new MavenScriptTask().getReleaseInfo();
    }

    public static final Task getJetpackSaasTriggerTask() {
        return new JetpackScriptTask().getJetpackSaasTrigger();
    }

    public static final Task getJetpackSaasBreakBuildTask() {
        return new JetpackScriptTask().getJetpackSaasBreakBuild();
    }

    protected String getFilePath(final String group, final String shellScript) {
        return BAMBOO_BUILD_DIR + group + shellScript;
    }

    protected String getFilePath(final String shellScript) {
        return BAMBOO_BUILD_DIR + shellScript;
    }

    protected Task getInlineScriptTask(final String description, final String inLineBody, final String variables) {
        final ScriptTask scriptTask = new ScriptTask().description(description).inlineBody(inLineBody);
        if (StringUtils.isNotEmpty(variables)) {
            scriptTask.environmentVariables(variables);
        }
        return scriptTask;
    }
    protected Task getFileScriptTask(final String description, final String filePath, final String variables) {
        return new ScriptTask().description(description).fileFromPath(filePath).environmentVariables(variables);
    }

    protected Task getFileScriptTask(final String description, final String filePath) {
        return new ScriptTask().description(description).fileFromPath(filePath);
    }
}
