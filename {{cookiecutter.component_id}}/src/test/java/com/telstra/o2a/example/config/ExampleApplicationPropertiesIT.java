package {{cookiecutter.group_id}}.example.config;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import {{cookiecutter.group_id}}.example.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        locations = {"classpath:application-test.yml"})
@DirtiesContext
@ActiveProfiles({"test"})
public class ExampleApplicationPropertiesIT {

    @Autowired
    ExampleApplicationProperties appProperties;

    @Test
    public void testApplicationBasePropertiesAreLoaded() {
        assertTrue(appProperties.getBasepath() != null);
        assertTrue(appProperties.getName() != null);
    }
}
