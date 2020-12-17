package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.List;

class LibBuildStage extends BambooStages {

    Stage getStage() {
        return new Stage("Lib Build Stage")
                .manual(false)
                .jobs(getBuildDockerImageJob());
    }

    private Job getBuildDockerImageJob() {
        return new Job("Build Lib", new BambooKey("BDI"))
                .tasks(getTasks())
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    private Task[] getTasks() {
        final List<Task> tasks = getLibMavenTaskList();
        return tasks.toArray(new Task[tasks.size()]);
    }
}
