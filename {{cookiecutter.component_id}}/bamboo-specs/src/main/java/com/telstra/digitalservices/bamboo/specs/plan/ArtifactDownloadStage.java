package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.LinkedList;
import java.util.List;

class ArtifactDownloadStage extends BambooStages {

    Stage getStage(final String projectKey, final String masterPlanKey) {
        return new Stage("Master Artifacts Stage")
                .manual(false)
                .jobs(getCoverityJob(projectKey, masterPlanKey));
    }

    private Job getCoverityJob(final String projectKey, final String masterPlanKey) {
        return new Job("Artifacts Download", new BambooKey("AFDL"))
                .tasks(getTasks(projectKey, masterPlanKey))
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    private Task[] getTasks(final String projectKey, final String masterPlanKey) {
        List<Task> tasks = new LinkedList<>();
        tasks.add(PlanTask.getEnvironmentScriptTask());
        tasks.add(getArtifactDownloaderTask(projectKey, masterPlanKey));
        tasks.add(PlanTask.getVariablesScriptTask());
        tasks.add(PlanTask.getInjectMasterVariablesTask());
        return tasks.toArray(new Task[tasks.size()]);
    }
}
