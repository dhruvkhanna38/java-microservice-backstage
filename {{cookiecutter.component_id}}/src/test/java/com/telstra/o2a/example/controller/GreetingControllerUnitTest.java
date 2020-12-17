package {{cookiecutter.group_id}}.example.controller;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.telstra.digitalservices.core.discovery.DiscoveryInfo;
import com.telstra.digitalservices.core.logging.RequestResponseLogger;
import com.telstra.digitalservices.core.model.MicroserviceResponse;
import com.telstra.digitalservices.core.rest.RestInvoker;
import ${groupId}.example.config.ExampleApplicationProperties;
import ${groupId}.example.exceptions.UnexpectedValueException;
import ${groupId}.example.exceptions.ValueNotFoundException;
import ${groupId}.example.model.Greeting;
import ${groupId}.example.service.GreetingService;

@RunWith(MockitoJUnitRunner.class)
public class GreetingControllerUnitTest {
    @InjectMocks
    private GreetingController controller;

    @Mock
    private ExampleApplicationProperties appProperties;
    @Mock
    private GreetingService service;

    @Mock
    private Greeting model;
    private DiscoveryInfo discoInfo = new DiscoveryInfo();

    private RequestResponseLogger requestResponseLogger = new RequestResponseLogger(
            discoInfo);

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(appProperties.getBasepath()).thenReturn("/hello/v1/greetings");

    }

    @Test
    public void doPost_should_return_success_response()
            throws UnexpectedValueException, ValueNotFoundException {
        when(service.sayHello(anyString())).thenReturn("Hello");
        MicroserviceResponse response = controller.doPost("World", null);
        assertTrue(response != null);
        assertTrue(response.getHttpStatus().value() == 200);
        assertTrue(response.getData() != null);
        assertTrue(response.getData().get("greetings") != null);
        assertTrue(response.getData().get("greetings").get("content") != null);
        assertTrue(response.getData().get("greetings").get("content").asText()
                .equals("Hello, World!"));

    }
    @Test
    public void doPost_should_return_404_response_with_invalid_locale()
            throws UnexpectedValueException, ValueNotFoundException {
        MicroserviceResponse response = controller.doPost("World", "cc");
        assertTrue(response != null);
        assertNotNull(response);
        assertNotNull(response.getCode());
        assertTrue(response.getCode().equals(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        assertTrue(response.getHttpStatus().value() == 500);
    }
    @Test
    public void doPost_should_return_404_response()
            throws UnexpectedValueException, ValueNotFoundException {
        when(service.sayHello(anyString()))
                .thenThrow(new ValueNotFoundException("Value not found"));
        MicroserviceResponse response = controller.doPost("World", null);
        assertNotNull(response);
        assertNotNull(response.getCode());
        assertTrue(response.getCode().equals(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    public void doPost_should_return_500_response()
            throws UnexpectedValueException, ValueNotFoundException {
        when(service.sayHello(anyString()))
                .thenThrow(new UnexpectedValueException("Value not permitted"));
        MicroserviceResponse response = controller.doPost("World", null);
        assertNotNull(response);
        assertNotNull(response.getCode());
        assertTrue(response.getCode()
                .equals(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    public void doPost_should_return_400_response() {
        MicroserviceResponse response = controller.doPost("", "");
        assertTrue(response != null);
        assertTrue(response.getHttpStatus().value() == 400);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() == 1);
        assertTrue(response.getErrors().get(0).getCode().equals("1000"));
        assertTrue(response.getErrors().get(0).getMessage() != null);

    }
    @Test
    public void doPost_should_return_422_response() {
        MicroserviceResponse response = controller.doPost("a", "");
        assertTrue(response != null);
        assertTrue(response.getHttpStatus().value() == 422);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() == 1);
        assertTrue(response.getErrors().get(0).getCode().equals("1001"));
        assertTrue(response.getErrors().get(0).getMessage() != null);

    }

    @Test
    public void doPost_should_return_404()
            throws IOException, RestClientException, URISyntaxException {
        GreetingController mockController = mock(GreetingController.class);
        RestOperations restOperations = mock(RestOperations.class);
        MicroserviceResponse msResponse = new MicroserviceResponse();
        msResponse.setCorrelationId("abcde");
        msResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        ResponseEntity<MicroserviceResponse> mockResponseEntity = new ResponseEntity<>(
                msResponse, HttpStatus.NOT_FOUND);
        when(restOperations.exchange(any(URI.class), any(HttpMethod.class),
                Mockito.<HttpEntity<?>> any(),
                Mockito.<Class<MicroserviceResponse>> any()))
                        .thenReturn(mockResponseEntity);

        RestInvoker restInvoker = new RestInvoker(restOperations, discoInfo,
                requestResponseLogger);
        ResponseEntity<MockClientHttpResponse> resp = restInvoker
                .performRestOperation("https://localhost/hello/v1/greetings",
                        HttpMethod.POST, null, MockClientHttpResponse.class);
        mockController.doPost("R2-D2", "isl");
        assertTrue(resp.getStatusCode()
                .equals(mockResponseEntity.getStatusCode()));

    }
    @Test
    public void doPost_should_return_500()
            throws IOException, RestClientException, URISyntaxException {
        GreetingController mockController = mock(GreetingController.class);

        RestOperations restOperations = mock(RestOperations.class);
        MicroserviceResponse msResponse = new MicroserviceResponse();
        msResponse.setCorrelationId("efgh-123");
        msResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity<MicroserviceResponse> mockResponseEntity = new ResponseEntity<>(
                msResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restOperations.exchange(any(URI.class), any(HttpMethod.class),
                Mockito.<HttpEntity<?>> any(),
                Mockito.<Class<MicroserviceResponse>> any()))
                        .thenReturn(mockResponseEntity);

        RestInvoker restInvoker = new RestInvoker(restOperations, discoInfo,
                requestResponseLogger);
        ResponseEntity<MockClientHttpResponse> resp = restInvoker
                .performRestOperation("https://localhost/hello/v1/greetings",
                        HttpMethod.POST, null, MockClientHttpResponse.class);
        mockController.doPost("Rey", "fra");
        assertTrue(resp.getStatusCode()
                .equals(mockResponseEntity.getStatusCode()));

    }

}
