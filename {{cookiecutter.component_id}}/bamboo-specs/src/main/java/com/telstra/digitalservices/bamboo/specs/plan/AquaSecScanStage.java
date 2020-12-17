package com.telstra.digitalservices.bamboo.specs.plan;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.telstra.digitalservices.bamboo.specs.plan.task.PlanTask;

import java.util.LinkedList;
import java.util.List;

class AquaSecScanStage extends BambooStages {

    Stage getStage() {
        return new Stage("AuqaSec Scan Stage")
                .manual(false)
                .jobs(getAuqaSecScanJob());
    }

    private Job getAuqaSecScanJob() {
        return new Job("AuqaSecScan", new BambooKey("AQSS"))
                .tasks(getTasks())
                .artifacts(new Artifact()
                                .name("summary.json")
                                .copyPattern("summary.json")
                                .shared(true)
                                .required(true),
                        new Artifact()
                                .name("output.json")
                                .copyPattern("output.json")
                                .shared(true)
                                .required(true))
                .requirements(BambooStages.DockerRequirement)
                .cleanWorkingDirectory(true);
    }

    private Task[] getTasks() {
        List<Task> tasks = new LinkedList<>();
        tasks.add(PlanTask.getEnvironmentScriptTask());
        tasks.add(PlanTask.getAquaSecScanScriptTask());
        return tasks.toArray(new Task[tasks.size()]);
    }
}
