import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;
import com.telstra.digitalservices.bamboo.specs.BambooSpecs;
import com.telstra.digitalservices.bamboo.specs.plan.BambooStages;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@BambooSpec
public class PlanSpec extends BambooSpecs {

    public PlanSpec() {
        final JSONObject jsonObject = getConfiguration();
        final String ProjectName = jsonObject.getString("projectName");
        final String PlanName = jsonObject.getString("planName");
        final String PlanKey = jsonObject.getString("planKey");

        ProjectKey = jsonObject.getString("projectKey");
        MasterPlanKey = jsonObject.optString("masterPlanKey");

        linkedRepository.add(jsonObject.getString("defaultLinkedRepository"));
        jsonObject.getJSONArray("linkedRepositories").forEach(o -> {linkedRepository.add((String) o);});

        final Map<String, PermissionType> groupPermissions = new HashMap<>();
        jsonObject.getJSONArray("groupPermissions").forEach(o -> {
            final JSONObject permission = (JSONObject)o;
            groupPermissions.put(permission.getString("name"), getPermissionType(permission.getString("type")));
        });

        final Map<String, PermissionType> userPermissions = new HashMap<>();
        jsonObject.getJSONArray("userPermissions").forEach(o -> {
            final JSONObject permission = (JSONObject)o;
            userPermissions.put(permission.getString("name"), getPermissionType(permission.getString("type")));
        });

        final Map<String, String> variables = BambooSpecs.getDefaultVariables();
        jsonObject.getJSONArray("variables").forEach(o -> {
            final JSONObject variable = (JSONObject)o;
            variables.put(variable.getString("key"), variable.getString("value"));
        });

        final List<Stage> stages = new LinkedList<>();
        stages.add(BambooStages.getCoverityStage());
        if (StringUtils.isNotEmpty(MasterPlanKey)) {
            stages.add(BambooStages.getArtifactDownloadStage(ProjectKey, MasterPlanKey));
        }
        jsonObject.getJSONArray("stages").forEach(o -> {
            final Stage stage = getStage((JSONObject)o);
            if (stage != null) {
                stages.add(stage);
                if ("BuildStage".equals(((JSONObject)o).getString("name"))) {
                    stages.add(BambooStages.getAuqaSecScanStage());
                }
            }
        });

        final Set<String> linkedRepositories = new LinkedHashSet<>();
        linkedRepositories.addAll(linkedRepository);

        final List<Trigger> triggers = new LinkedList<>();
        triggers.add(new BitbucketServerTrigger().enabled(false));

        this.withProject(ProjectName, ProjectKey)
                .withPlan(PlanName, PlanKey)
                .withStages(stages)
                .withLinkedRepositories(linkedRepositories)
                .withTriggers(triggers)
                .withUserPermissions(userPermissions)
                .withGroupPermissions(groupPermissions)
                .withVariables(variables);

        if (jsonObject.opt("deployment") != null) {
            final JSONObject deployment = jsonObject.getJSONObject("deployment");
            final List<String> deploymentEnvironments = new LinkedList<>();
            deployment.getJSONArray("env").forEach(o -> {
                deploymentEnvironments.add((String)o);
            });
            if(deploymentEnvironments.indexOf("SVTCICD") > -1) {
                final Set<String>  testTypes = new HashSet<>();
                jsonObject.getJSONArray("svtCicd").forEach(o -> {
                    testTypes.add((String)o);
                });
                this.withSVTTestTypes(testTypes, linkedRepository.get(0));
            }
            final String linkedRepository = deployment.optString("linkedRepository");
            final String deployFilePath = deployment.optString("filePath");
            withDeploymentProject(deploymentEnvironments, deployFilePath, linkedRepository);
        }
        this.build();
    }

    private final static String BAMBOO_SPECS_CONFIURATION = "bamboo_specs.json";
    private final List<String> linkedRepository = new ArrayList<>();
    private String MasterPlanKey;
    private String ProjectKey;

    private PermissionType getPermissionType(String type) {
        switch (type) {
            case "ADMIN":
                return PermissionType.ADMIN;
            case "BUILD":
                return PermissionType.BUILD;
            default:
                return PermissionType.VIEW;
        }
    }

    private Stage getStage(final JSONObject stage) {
        switch (stage.getString("name")) {
            case "MasterArtifactDownloadStage":
                return null;//return BambooStages.getArtifactDownloadStage(ProjectKey, MasterPlanKey);
            case "CoverityStage":
                return null;//return BambooStages.getCoverityStage();
            case "LibBuildStage":
                return BambooStages.getLibBuildStage();
            case "BuildStage":
                final String buildFilePath = stage.optString("filePath");
                if(StringUtils.isEmpty(buildFilePath)) {
                    return BambooStages.getBuildStage();
                } else {
                    return BambooStages.getBuildStage(buildFilePath);
                }
            case "DeployStage":
                final String deployFilePath = stage.getString("filePath");
                if(StringUtils.isEmpty(deployFilePath)) {
                    throw new RuntimeException("There is no deploy file path.");
                }
                return BambooStages.getDeployStage(stage.getString("env"), stage.getBoolean("isManual"), deployFilePath);
            case "SVTCICDStage":
                final List<String> testTypes = new LinkedList<>();
                stage.getJSONArray("testType").forEach(o -> {
                    testTypes.add((String)o);
                });
                return BambooStages.getSVTStage(testTypes, stage.getBoolean("isManual"), linkedRepository.get(0));
            case "CRQiTAMStage":
                return BambooStages.getCRQiTAMStage(stage.getBoolean("isManual"), linkedRepository.get(0));
            default:
                throw new RuntimeException("StageName["+stage.getString("name")+"] is wrong.");
        }
    }

    private JSONObject getConfiguration() {
        final URL resource = getClass().getClassLoader().getResource(BAMBOO_SPECS_CONFIURATION);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            final File file = new File(resource.getFile());
            try(InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
                return new JSONObject(IOUtils.toString(reader));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String... argv) {
        new PlanSpec();
    }
}
