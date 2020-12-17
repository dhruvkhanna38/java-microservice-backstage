package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class DeployStage extends BambooStages {

    Stage getStage(final String env, final boolean isManual, final String filePath, final String repositoryName) {
        return new Stage(String.format("%s Stage", env.toUpperCase()))
                .manual(isManual)
                .jobs(getDeployJob(env, filePath, repositoryName));
    }

    private Job getDeployJob(final String env, final String filePath, final String repositoryName) {
        return new Job(String.format("Deploy On %s", env.toUpperCase()), new BambooKey(env.toUpperCase()))
                .tasks(getTasks(env, filePath, repositoryName))
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    public Task[] getTasks(final String env, final String filePath, final String repositoryName) {
        final List<Task> tasks = getDeployTaskList();
        if (StringUtils.isNotEmpty(filePath) && StringUtils.isNotEmpty(repositoryName)) {
            tasks.add(PlanTask.getDeployCheckoutTask(repositoryName));
        } else {
            tasks.add(PlanTask.getEnvironmentScriptTask());
            tasks.add(PlanTask.getMasterSharedImageTask());
        }
        tasks.add(PlanTask.getDeployTask(env, filePath));
        return tasks.toArray(new Task[tasks.size()]);
    }
}
