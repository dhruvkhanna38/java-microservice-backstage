package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.LinkedList;
import java.util.List;

class BuildStage extends BambooStages {

    Stage getStage(final String filePath) {
        return new Stage("Build Stage")
                .manual(false)
                .jobs(getBuildDockerImageJob(filePath));
    }

    private Job getBuildDockerImageJob(final String filePath) {
        return new Job("Build Docker Image", new BambooKey("BDI"))
                .tasks(getTasks(filePath))
                .artifacts(new Artifact().name("Docker image Info").copyPattern("docker-image.json").shared(true).required(false))
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    private Task[] getTasks(final String filePath) {
        final List<Task> tasks = getMavenTaskList();
        tasks.add(PlanTask.getDockerImageScriptTask(filePath));
        addJetPackSaasTaskList(tasks);
        return tasks.toArray(new Task[tasks.size()]);
    }
}
