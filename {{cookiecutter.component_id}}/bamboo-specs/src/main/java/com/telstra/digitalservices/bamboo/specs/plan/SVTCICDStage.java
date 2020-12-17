package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.List;

class SVTCICDStage extends BambooStages {

    Stage getStage(final List<String> testTypes, final boolean isManual, final String repositoryName) {
        return new Stage(String.format("SVT CICD Stage"))
                .manual(isManual)
                .jobs(getDeployJob(testTypes, repositoryName));
    }

    private Job getDeployJob(final List<String> testTypes, final String repositoryName) {
        return new Job("Launch SVT CICD Test", new BambooKey("SVTCICD"))
                .tasks(getTasks(testTypes, repositoryName))
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    public Task[] getTasks(final List<String> testTypes, final String repositoryName) {
        final List<Task> tasks = getDeployTaskList();
        tasks.add(PlanTask.getEnvironmentScriptTask());
        tasks.add(PlanTask.getDeployCheckoutTask(repositoryName));
        testTypes.forEach(testType -> {
            tasks.add(PlanTask.getSVTCICDTask(testType));
        });
        return tasks.toArray(new Task[tasks.size()]);
    }
}
