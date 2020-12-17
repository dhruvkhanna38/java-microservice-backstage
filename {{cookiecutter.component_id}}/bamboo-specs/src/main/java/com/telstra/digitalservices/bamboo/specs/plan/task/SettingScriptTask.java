package com.telstra.digitalservices.bamboo.specs.plan.task;

import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.*;
import com.atlassian.bamboo.specs.model.task.InjectVariablesScope;

public class SettingScriptTask extends PlanTask {

    private static final String DESCRIPTION_SET_MAVEN_ENVIRONMENT = "Set Maven Environment";
    private static final String SET_MAVEN_ENVIRONMENT = "m2-cache-builder.sh";

    private static final String DESCRIPTION_SET_VARIABLES = "Set Variables";
    private static final String SET_VARIABLES = "variables.sh";

    private static final String DESCRIPTION_SVT_CICD = "Launch svt CICD";
    private static final String SVT_CICD = "svt_cicd/launch.sh";

    private static final String DESCRIPTION_CRQ_iTAM = "Create CRQ iTAM";
    private static final String CRQ_iTAM = "crq_itam/create.sh";

    private static final String DESCRIPTION_SET_ENVIRONMENT = "Set Environment";
    private static final String DESCRIPTION_DEPLOY_SERVICE = "Deploy a Service";
    private static final String DEPLOY_SHELL = "deploy_shell.sh";
    private static final String DESCRIPTION_BUILD_DOCKER_IMAGE = "Build DockerImage";

    private static final String BUILD_DOCKER_IMAGE = "docker_image.sh";
    private static final String DOCKER_BUILD_SHELL = "docker_build_shell.sh";

    private static final String DESCRIPTION_COVERITY = "Coverity";
    private static final String COVERITY = "coverity.sh";

    private static final String DESCRIPTION_AQUASEC = "AquaSec";
    private static final String AQUASEC = "aquasec/scan.sh";

    private static final String DESCRIPTION_MASTER_SHARE = "Pull Master Shared Image";
    private static final String MASTER_SHARE = "pullMasterShareImage.sh";

    private static final String DESCRIPTION_GIT_COMMIT = "Git Commit - Next development iteration";
    private static final String DESCRIPTION_GIT_TAG = "Git Tag - Release version";
    private static final String DESCRIPTION_GIT_PUSH = "Git Push";
    private static final String DESCRIPTION_INJECT_RELEASE_VARIABLES = "Inject Release Variables";

    Task getEnvironment() {
        return getInlineScriptTask(DESCRIPTION_SET_ENVIRONMENT, getEnvironmentBody(), "");
    }

    private String getEnvironmentBody() {
        return new StringBuilder()
                .append("#!/bin/bash").append("\n\n")
                .append("BAMBOO_ENV_BUILDER=").append("${bamboo_BAMBOO_ENV_BUILDER:-'amp-bamboo-build:1.0.1'}").append("\n")
                .append("BAMBOO_ENV_BUILDER=").append(DOCKER_REGIDTRY).append("/${BAMBOO_ENV_BUILDER}").append("\n")
                .append("docker pull ${BAMBOO_ENV_BUILDER}").append("\n")
                .append("docker run -t --rm -v $HOME:/target ${BAMBOO_ENV_BUILDER} /bin/sh -c 'cp -r /bamboo_build /target/;'").append("\n")
                .toString();
    }

    Task getMasterSharedImage() {
        return getFileScriptTask(DESCRIPTION_MASTER_SHARE, getFilePath(SCRIPT_GROUP, MASTER_SHARE));
    }


    Task getVariables() {
        return getFileScriptTask(DESCRIPTION_SET_VARIABLES, getFilePath(SCRIPT_GROUP, SET_VARIABLES));
    }

    Task getSVTCICD(final String testType) {
        final String variables = "TEST_TYPE=\"" + testType.toLowerCase() + "\"";
        return getFileScriptTask(DESCRIPTION_SVT_CICD, getFilePath(SCRIPT_GROUP, SVT_CICD), variables);
    }

    Task getCRQiTAMS() {
        return getFileScriptTask(DESCRIPTION_CRQ_iTAM, getFilePath(SCRIPT_GROUP, CRQ_iTAM));
    }

    Task getMavenEnvironment() {
        return getFileScriptTask(DESCRIPTION_SET_MAVEN_ENVIRONMENT, getFilePath(M2_GROUP, SET_MAVEN_ENVIRONMENT));
    }

    Task getCoverity() {
        return getFileScriptTask(DESCRIPTION_COVERITY, getFilePath(SCRIPT_GROUP, COVERITY));
    }

    Task getAquaSecScan() {
        return getFileScriptTask(DESCRIPTION_AQUASEC, getFilePath(SCRIPT_GROUP, AQUASEC));
    }

    Task getGitCommit() {
        return new VcsCommitTask()
                .description(DESCRIPTION_GIT_COMMIT)
                .defaultRepository()
                .commitMessage("[maven-release] prepare for next development iteration");
    }

    Task getGitTag() {
        return new VcsTagTask()
                .description(DESCRIPTION_GIT_TAG)
                .defaultRepository()
                .tagName("v${bamboo.releaseInfo.version}");
    }

    Task getGitPush() {
        return new VcsPushTask().description(DESCRIPTION_GIT_PUSH).defaultRepository();
    }

    Task getInjectVariables() {
        return new InjectVariablesTask().description(DESCRIPTION_INJECT_RELEASE_VARIABLES)
                .path("release-info.properties")
                .namespace("releaseInfo")
                .scope(InjectVariablesScope.RESULT);
    }

    Task getInjectMasterVariables() {
        return new InjectVariablesTask().description(DESCRIPTION_INJECT_RELEASE_VARIABLES)
                .path("variables")
                .namespace("master")
                .scope(InjectVariablesScope.RESULT);
    }

    Task getDockerImage(final String filePath) {
        if (filePath != null){
            return new ScriptTask()
                    .description(DESCRIPTION_BUILD_DOCKER_IMAGE)
                    .environmentVariables("DOCKER_BUILD_SHELL=\"" + filePath + "\"")
                    .fileFromPath(getFilePath(SCRIPT_GROUP, DOCKER_BUILD_SHELL));
        } else {
            return getFileScriptTask(DESCRIPTION_BUILD_DOCKER_IMAGE, getFilePath(SCRIPT_GROUP, BUILD_DOCKER_IMAGE));
        }
    }

    Task getDeployCheckout(final String repositoryName) {
        return new VcsCheckoutTask()
                .description("Source Code Checkout")
                .checkoutItems(new CheckoutItem().repository(new VcsRepositoryIdentifier().name(repositoryName)));
    }

    Task getDeploy(final String env, final String filePath) {
        return new ScriptTask()
                .description(DESCRIPTION_DEPLOY_SERVICE)
                .environmentVariables("AMP_ENV=\"" + env.toLowerCase() + "\" BUILD_SHELL=\"" + filePath + "\"")
                .fileFromPath(getFilePath(SCRIPT_GROUP, DEPLOY_SHELL));
    }
}
