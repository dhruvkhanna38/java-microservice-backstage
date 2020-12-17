package com.telstra.digitalservices.bamboo.specs.plan.task;

import com.atlassian.bamboo.specs.api.builders.task.Task;

public class MavenScriptTask extends PlanTask {

    private static final String DESCRIPTION_MAVEN_TEST = "Maven Test";
    private static final String DESCRIPTION_MAVEN_BUILD = "Maven Build";
    private static final String DESCRIPTION_MAVEN_RELEASE = "Maven Release";
    private static final String DESCRIPTION_DEPLOY_ARTIFACT = "Deploy Artifact";
    private static final String DESCRIPTION_RELEASE_INFO = "Release version";

    private static final String MAVEN_TEST = "mvn-test.sh";
    private static final String MAVEN_BUILD = "mvn-build.sh";
    private static final String MAVEN_RELEASE = "mvn-release.sh";
    private static final String DEPLOY_LIB = "deploy-lib.sh";
    private static final String RELEASE_INFO = "release-info.sh";

    Task getMavenTest() {
        return getFileScriptTask(DESCRIPTION_MAVEN_TEST, getFilePath(SCRIPT_GROUP, MAVEN_TEST));
    }

    Task getMavenBuild() {
        return getFileScriptTask(DESCRIPTION_MAVEN_BUILD, getFilePath(SCRIPT_GROUP, MAVEN_BUILD));
    }

    Task getMavenRelease() {
        return getFileScriptTask(DESCRIPTION_MAVEN_RELEASE, getFilePath(SCRIPT_GROUP, MAVEN_RELEASE));
    }

    Task getDeployLib() {
        return getFileScriptTask(DESCRIPTION_DEPLOY_ARTIFACT, getFilePath(SCRIPT_GROUP, DEPLOY_LIB));
    }

    Task getReleaseInfo () {
        return getFileScriptTask(DESCRIPTION_RELEASE_INFO, getFilePath(SCRIPT_GROUP, RELEASE_INFO));
    }
}
