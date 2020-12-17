package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.List;
import java.util.Set;

class CRQiTAMStage extends BambooStages {

    Stage getStage(final boolean isManual, final String repositoryName) {
        return new Stage(String.format("CRQ iTAM Stage"))
                .manual(isManual)
                .jobs(getDeployJob(repositoryName));
    }

    private Job getDeployJob(final String repositoryName) {
        return new Job("Launch CRQ iTAM", new BambooKey("CRQITEM"))
                .tasks(getTasks(repositoryName))
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    public Task[] getTasks(final String repositoryName) {
        final List<Task> tasks = getDeployTaskList();
        tasks.add(PlanTask.getEnvironmentScriptTask());
        tasks.add(PlanTask.getDeployCheckoutTask(repositoryName));
        tasks.add(PlanTask.getCRQiTAMTask());
        return tasks.toArray(new Task[tasks.size()]);
    }
}
