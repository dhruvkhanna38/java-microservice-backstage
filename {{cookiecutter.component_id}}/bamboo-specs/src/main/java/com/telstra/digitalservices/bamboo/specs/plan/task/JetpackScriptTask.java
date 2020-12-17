package com.telstra.digitalservices.bamboo.specs.plan.task;

import com.atlassian.bamboo.specs.api.builders.task.Task;

public class JetpackScriptTask extends PlanTask {
    private static final String DESCRIPTION_JETPACK_SAAS_TRIGGER = "Jetpack SaaS Trigger";
    private static final String DESCRIPTION_JETPACK_BREAK_BUILD = "Jetpack Saas Break Build";

    private static final String JETPACK_SAAS_TRIGGER = "jetpack_saas_trigger.sh";
    private static final String JETPACK_BREAK_BUILD = "jetpack_saas_break_build.sh";

    Task getJetpackSaasTrigger() {
        return getFileScriptTask(DESCRIPTION_JETPACK_SAAS_TRIGGER, getFilePath(JETPACK_SCRIPT_GROUP, JETPACK_SAAS_TRIGGER));
    }

    Task getJetpackSaasBreakBuild() {
        return getFileScriptTask(DESCRIPTION_JETPACK_BREAK_BUILD, getFilePath(JETPACK_SCRIPT_GROUP, JETPACK_BREAK_BUILD));
    }
}
