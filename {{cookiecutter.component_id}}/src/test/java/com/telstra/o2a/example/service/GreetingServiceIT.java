package {{cookiecutter.group_id}}.example.service;
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
import {{cookiecutter.group_id}}.example.exceptions.UnexpectedValueException;
import {{cookiecutter.group_id}}.example.exceptions.ValueNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        locations = {"classpath:application-test.yml"})
@DirtiesContext
@ActiveProfiles({"test"})
public class GreetingServiceIT {

    @Autowired
    GreetingService service;

    @Test
    public void verify_return_not_value_when_pass_null()
            throws UnexpectedValueException, ValueNotFoundException {
        assertTrue(service.sayHello(null) != null);
        assertTrue(service.sayHello(null).equalsIgnoreCase("Hi"));

    }
    @Test
    public void verify_return_expected_value()
            throws UnexpectedValueException, ValueNotFoundException {
        assertTrue(service.sayHello("eng") != null);
        assertTrue(service.sayHello("eng").equalsIgnoreCase("Hello"));
        assertTrue(service.sayHello("deu") != null);
        assertTrue(service.sayHello("deu").equalsIgnoreCase("Hallo"));

    }
    @Test(expected = UnexpectedValueException.class)
    public void verify_return_UnexpectedValueException()
            throws UnexpectedValueException, ValueNotFoundException {
        assertTrue(service.sayHello("fra") != null);
    }
    @Test(expected = ValueNotFoundException.class)
    public void verify_return_ValueNotFoundException()
            throws UnexpectedValueException, ValueNotFoundException {
        assertTrue(service.sayHello("isl") != null);
    }
}
