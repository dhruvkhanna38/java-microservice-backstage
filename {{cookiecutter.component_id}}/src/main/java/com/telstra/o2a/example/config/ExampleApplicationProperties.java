package ${groupId}.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "telstra.microservice")
public class ExampleApplicationProperties {
    //@NotNull
    private String basepath;
   // @NotNull
    private String name;
    public void setBasepath(final String basepath) {
        this.basepath = basepath;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public String getBasepath() {
        return basepath;
    }
    public String getName() {
        return name;
    }

}
