package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.List;

class CoverityStage extends BambooStages {

    Stage getStage() {
        return new Stage("Coverity Stage")
                .manual(false)
                .jobs(getCoverityJob());
    }

    private Job getCoverityJob() {
        return new Job("Coverity", new BambooKey("COV"))
                .tasks(getTasks())
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    private Task[] getTasks() {
        List<Task> tasks = getDefaultTaskList();
        tasks.add(PlanTask.getCoverityScriptTask());
        return tasks.toArray(new Task[tasks.size()]);
    }
}
