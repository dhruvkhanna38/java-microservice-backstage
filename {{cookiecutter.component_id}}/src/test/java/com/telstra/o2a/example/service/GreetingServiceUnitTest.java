package {{cookiecutter.group_id}}.example.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import {{cookiecutter.group_id}}.example.exceptions.UnexpectedValueException;
import {{cookiecutter.group_id}}.example.exceptions.ValueNotFoundException;

public class GreetingServiceUnitTest {

    private GreetingService service = new GreetingService();
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

    }
    @Test
    public void should_return_valid_response()
            throws UnexpectedValueException, ValueNotFoundException {

        assertNotNull(service.sayHello(null));
        assertTrue(service.sayHello(null).equals("Hi"));

        assertNotNull(service.sayHello("eng"));
        assertTrue(service.sayHello("eng").equals("Hello"));

        assertNotNull(service.sayHello("en"));
        assertTrue(service.sayHello("en").equals("Hello"));

        assertNotNull(service.sayHello("jpn"));
        assertTrue(service.sayHello("jpn").equals("Kon'nichiwa"));

        assertNotNull(service.sayHello("deu"));
        assertTrue(service.sayHello("deu").equals("Hallo"));

    }
    @Test(
            expected = UnexpectedValueException.class)
    public void should_return_expected_RuntimeException()
            throws UnexpectedValueException, ValueNotFoundException {
        service.sayHello("fra");

    }
    @Test(
            expected = ValueNotFoundException.class)
    public void should_return_expected_ValueNotFoundException()
            throws UnexpectedValueException, ValueNotFoundException {
        service.sayHello("isl");

    }

}
