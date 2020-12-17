package com.telstra.digitalservices.bamboo.specs;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.RootEntityPropertiesBuilder;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.*;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.telstra.digitalservices.bamboo.specs.plan.BambooStages;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BambooSpecs {

    private final static String BAMBOO_SERVER = "https://bamboo.tools.telstra.com";
    private final BambooServer bambooServer = new BambooServer(BAMBOO_SERVER);

    private String projectName;
    private String projectKey;
    private String planName;
    private String planKey;
    private Plan plan;
    private String DeployShellFilePath = null;
    private String RepositoryName = null;
    private String DefaultLinkedRepository = null;
    private List<String> DeploymentEnvironments = null;
    private List<String> SVTTestTypes = null;
    private final List<Stage> stages = new LinkedList<>();
    private final Set<String> linkedRepositories = new LinkedHashSet<>();
    private final List<Trigger> triggers = new LinkedList<>();
    private PlanBranchManagement branchManagement;
    private final Map<String, PermissionType> userPermissions = new HashMap<>();
    private final Map<String, PermissionType> groupPermissions = new HashMap<>();
    private final Map<String, String> variables = new HashMap<>();

    public final <T extends BambooSpecs> T publish(@NotNull RootEntityPropertiesBuilder entityProperties) {
        bambooServer.publish(entityProperties);
        return (T)this;
    }

    public final <T extends BambooSpecs> T withProject(final String projectName, final String projectKey) {
        this.projectName = projectName;
        this.projectKey = projectKey;
        return (T)this;
    }

    public final <T extends BambooSpecs> T withPlan(final String planName, final String planKey) {
        this.planName = planName;
        this.planKey = planKey;
        return (T)this;
    }

    public final <T extends BambooSpecs> T withStages(final List<Stage> stages) {
        if (stages != null) {
            this.stages.addAll(stages);
        }
        return (T)this;
    }

    public final <T extends BambooSpecs> T withLinkedRepositories(final Set<String> linkedRepositories) {
        if (linkedRepositories != null) {
            this.linkedRepositories.addAll(linkedRepositories);
        }
        return (T)this;
    }

    public final <T extends BambooSpecs> T withTriggers(final List<Trigger> triggers) {
        if (triggers != null) {
            this.triggers.addAll(triggers);
        }
        return (T)this;
    }

    public final <T extends BambooSpecs> T withBranchManagement(final PlanBranchManagement branchManagement) {
        this.branchManagement = branchManagement;
        return (T)this;
    }

    public final <T extends BambooSpecs> T withUserPermissions(final Map<String, PermissionType> userPermissions) {
        if (userPermissions != null) {
            this.userPermissions.putAll(userPermissions);
        }
        return (T)this;
    }

    public final <T extends BambooSpecs> T withGroupPermissions(final Map<String, PermissionType> groupPermissions) {
        if (groupPermissions != null) {
            this.groupPermissions.putAll(groupPermissions);
        }
        return (T)this;
    }

    public final <T extends BambooSpecs> T withVariables(final Map<String, String> variables) {
        if (variables != null) {
            this.variables.putAll(variables);
        }
        return (T)this;
    }


    public final <T extends BambooSpecs> T withDeploymentProject(final List<String> deploymentEnvironments,
                                                                 final String deployShellFilePath,
                                                                 final String repositoryName) {
        this.RepositoryName = repositoryName;
        return withDeploymentProject(deploymentEnvironments, deployShellFilePath);
    }

    public final <T extends BambooSpecs> T withDeploymentProject(final List<String> deploymentEnvironments,
                                                                 final String deployShellFilePath) {
        this.DeployShellFilePath = deployShellFilePath;
        return withDeploymentProject(deploymentEnvironments);
    }

    public final <T extends BambooSpecs> T withDeploymentProject(final List<String> deploymentEnvironments) {
        if (deploymentEnvironments != null) {
            this.DeploymentEnvironments = new LinkedList<>();
            deploymentEnvironments.forEach(env -> {
                if (this.DeployShellFilePath.indexOf(env.toUpperCase()) < 0) {
                    this.DeploymentEnvironments.add(env.toUpperCase());
                }
            });
        }
        return (T)this;
    }

    public final <T extends BambooSpecs> T withSVTTestTypes(final Set<String> SVTTestTypes, final String defaultLinkedRepository) {
        if (SVTTestTypes != null) {
            this.SVTTestTypes = new LinkedList<>();
            SVTTestTypes.forEach(testType -> {
                if (this.SVTTestTypes.indexOf(testType.toUpperCase()) < 0) {
                    this.SVTTestTypes.add(testType.toUpperCase());
                }
            });
            this.DefaultLinkedRepository = defaultLinkedRepository;
        }
        return (T)this;
    }

    public static Map<String, String> getDefaultVariables() {
        final Map<String, String> variables = new HashMap<>();
        variables.put("BAMBOO_ENV_BUILDER", "amp-bamboo-build:1.0.1");
        variables.put("SCANNER_TO_BREAK_BUILD", "Sonarqube");
        return variables;
    }

    public Plan getPlan() {
        return plan;
    }

    public void build() {
        plan = buildPlan();
        if (this.DeploymentEnvironments != null && this.DeploymentEnvironments.size() > 0) {
            buildDeployment();
        }
    }

    private Deployment buildDeployment() {
        final String deploymentprojectName = planName;
        final Deployment deployment = new Deployment(new PlanIdentifier(projectKey, planKey), deploymentprojectName)
                .releaseNaming(new ReleaseNaming("v${bamboo.releaseInfo.version}"))
                .environments(getEnvironments());
        bambooServer.publish(deployment);
        bambooServer.publish(getDeploymentPermissions(deploymentprojectName));
        DeploymentEnvironments.forEach(env -> {
            bambooServer.publish(getEnvironmentPermissions(deploymentprojectName, env));
        });
        return deployment;
    }

    private Environment[] getEnvironments() {
        List<Environment> environments = new LinkedList<>();
        DeploymentEnvironments.forEach(env -> {
            environments.add(getEnvironment(env));
        });
        return environments.toArray(new Environment[environments.size()]);
    }

    private Environment getEnvironment(final String env) {
        switch (env) {
            case "CRQiTAM":
                return new Environment(env)
                        .tasks(BambooStages.getCRQiTAMTasks(DefaultLinkedRepository));
            case "SVTCICD":
                return new Environment(env)
                        .tasks(BambooStages.getSVTCICDTasks(SVTTestTypes, DefaultLinkedRepository));
            default:
                return new Environment(env)
                        .tasks(BambooStages.getDeploymentTasks(env, DeployShellFilePath, RepositoryName))
                        .variables(new Variable("AMP_ENV", env.toLowerCase()));
        }
    }

    private DeploymentPermissions getDeploymentPermissions(final String deploymentprojectName) {
        return new DeploymentPermissions(deploymentprojectName)
                .permissions(getPermissions());
    }

    private EnvironmentPermissions getEnvironmentPermissions(final String deploymentprojectName, final String env) {
        return new EnvironmentPermissions(deploymentprojectName)
                .environmentName(env)
                .permissions(getPermissions());
    }

    private Plan buildPlan() {
        final Project project = new Project()
                .key(new BambooKey(projectKey))
                .name(projectName);
        final Plan plan = new Plan(project, planName, new BambooKey(planKey))
                .pluginConfigurations(new ConcurrentBuilds())
                .planBranchManagement(getPlanBranchManagement())
                .forceStopHungBuilds();
        setStages(plan);
        setTriggers(plan);
        setLinkedRepositories(plan);
        setVariables(plan);
        bambooServer.publish(plan);

        publishPlanPermissions();
        return plan;
    }

    private Plan setStages(final Plan plan) {
        if (stages != null && stages.size() > 0) {
            plan.stages(stages.toArray(new Stage[stages.size()]));
        }
        return plan;
    }

    private Plan setLinkedRepositories(final Plan plan) {
        if (linkedRepositories != null && linkedRepositories.size() > 0) {
            plan.linkedRepositories(linkedRepositories.toArray(new String[linkedRepositories.size()]));
        }
        return plan;
    }

    private Plan setTriggers(final Plan plan) {
        if(triggers != null && triggers.size() > 0) {
            plan.triggers(triggers.toArray(new Trigger[triggers.size()]));
        }
        return plan;
    }

    private PlanBranchManagement getPlanBranchManagement() {
        if (branchManagement == null) {
            return new PlanBranchManagement()
                    .delete(new BranchCleanup())
                    .notificationForCommitters();
        } else {
            return branchManagement;
        }
    }

    private Plan setVariables(final Plan plan) {
        if (variables != null && variables.size() > 0) {
            List<Variable> variableList = new LinkedList<>();
            variables.forEach((key, value) -> {
                variableList.add(new Variable(key, value));
            });
            plan.variables(variableList.toArray(new Variable[variableList.size()]));
        }
        return plan;
    }

    private Permissions getPermissions() {
        final Permissions permissions = new Permissions().loggedInUserPermissions(PermissionType.VIEW);
        userPermissions.forEach((key, value) -> {
            permissions.userPermissions(key, getDeploymentPermissionTypes(value));
        });
        groupPermissions.forEach((key, value) -> {
            permissions.groupPermissions(key, getDeploymentPermissionTypes(value));
        });
        return permissions;
    }

    protected void publishPlanPermissions() {
        final Permissions permissions = new Permissions().loggedInUserPermissions(PermissionType.VIEW);
        userPermissions.forEach((key, value) -> {
            permissions.userPermissions(key, getPermissionTypes(value));
        });
        groupPermissions.forEach((key, value) -> {
            permissions.groupPermissions(key, getPermissionTypes(value));
        });
        bambooServer.publish(new PlanPermissions(new PlanIdentifier(projectKey, planKey)).permissions(permissions));
    }

    private PermissionType[] getPermissionTypes(final PermissionType type) {
        final Set<PermissionType> permissionTypes = new HashSet<>();
        switch (type) {
            case ADMIN:
                permissionTypes.add(PermissionType.ADMIN);
            case EDIT:
                permissionTypes.add(PermissionType.EDIT);
                permissionTypes.add(PermissionType.CLONE);
            case BUILD:
                permissionTypes.add(PermissionType.BUILD);
            case VIEW:
                permissionTypes.add(PermissionType.VIEW);
        }
        return permissionTypes.toArray(new PermissionType[permissionTypes.size()]);
    }

    private PermissionType[] getDeploymentPermissionTypes(final PermissionType type) {
        final Set<PermissionType> permissionTypes = new HashSet<>();
        switch (type) {
            case ADMIN:
            case EDIT:
                permissionTypes.add(PermissionType.EDIT);
            case BUILD:
                permissionTypes.add(PermissionType.BUILD);
            case VIEW:
                permissionTypes.add(PermissionType.VIEW);
        }
        return permissionTypes.toArray(new PermissionType[permissionTypes.size()]);
    }
}
