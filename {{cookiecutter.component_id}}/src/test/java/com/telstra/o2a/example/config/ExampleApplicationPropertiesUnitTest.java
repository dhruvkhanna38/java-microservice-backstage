package {{cookiecutter.group_id}}.example.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExampleApplicationPropertiesUnitTest {
    @Mock
    ExampleApplicationProperties appProperties;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void verify_application_properties_canbe_assessed() {
        when(appProperties.getBasepath()).thenReturn("/hello/v1");
        when(appProperties.getName()).thenReturn("java-ms-project-template");
        // Test
        assertEquals("/hello/v1", appProperties.getBasepath());
        assertEquals("java-ms-project-template", appProperties.getName());

        verify(appProperties, times(1)).getBasepath();
        verify(appProperties, times(1)).getName();
    }
    @Test
    public void verify_application_properties_canbe_set() {

        doNothing().when(appProperties).setBasepath("/hello/v1");
        doNothing().when(appProperties).setName("java-ms-project-template");
        // Test
        appProperties.setBasepath("/hello/v1");
        appProperties.setName("java-ms-project-template");
        verify(appProperties, times(1)).setBasepath("/hello/v1");
        verify(appProperties, times(1)).setName("java-ms-project-template");
    }
    @Test
    public void verify_application_properties_are_not_null() {
        ExampleApplicationProperties appProperties = new ExampleApplicationProperties();
        appProperties.setBasepath("/hello/v1");
        appProperties.setName("java-ms-project-template");

        // Test
        assertNotNull(appProperties.getBasepath());
        assertEquals("/hello/v1", appProperties.getBasepath());
        assertNotNull(appProperties.getName());
        assertEquals("java-ms-project-template", appProperties.getName());

    }

}
