package ${groupId}.example.controller;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.telstra.digitalservices.core.model.MicroserviceResponse;
import com.telstra.digitalservices.core.template.MicroserviceController;
import com.telstra.digitalservices.core.template.MicroserviceMethod;
import {{cookiecutter.group_id}}.example.config.ExampleApplicationProperties;
import {{cookiecutter.group_id}}.example.exceptions.ValueNotFoundException;
import {{cookiecutter.group_id}}.example.model.Greeting;
import {{cookiecutter.group_id}}.example.service.GreetingService;
import {{cookiecutter.group_id}}.example.utils.Convertor;
import {{cookiecutter.group_id}}.example.utils.Validate;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@MicroserviceController
public class GreetingController {

    private static final String TEMPLATE = "%1$s, %2$s!";
    public static final int STATUS_200 = 200;
    public static final int STATUS_400 = 400;
    public static final int STATUS_404 = 404;
    public static final int STATUS_422 = 422;
    public static final int STATUS_500 = 500;
    public static final String MISSING_PARAMETER_ERROR_MSG = "Required String parameter 'name' is not present";
    public static final String INVALID_PARAMETER_ERROR_MSG = "Required String parameter 'name' is not valid."
            + " The length must be greater than 1";
    private final AtomicLong counter = new AtomicLong();

    private ExampleApplicationProperties appProperties;
    private GreetingService service;

    @Autowired
    public GreetingController(final ExampleApplicationProperties appProperties,
              final GreetingService service) {
        this.appProperties = appProperties;
        this.service = service;
    }

    @CrossOrigin
    @MicroserviceMethod
    @PostMapping(
            value = "/greetings",
            produces = "application/json")
    @ApiOperation(
            value = "GenerateGreetings",
            notes = "This API generates a greeting message")

    @ApiResponses(
            value = {@ApiResponse(
                    code = STATUS_200,
                    message = "Success"),
                    @ApiResponse(
                            code = STATUS_400,
                            message = "Bad Request"),
                    @ApiResponse(
                            code = STATUS_404,
                            message = "Not Found"),
                    @ApiResponse(
                            code = STATUS_422,
                            message = "Unprocessable Entity"),
                    @ApiResponse(
                            code = STATUS_500,
                            message = "Internal Server Error")})
    public MicroserviceResponse doPost(@RequestParam(
            value = "name") final String name,
            @RequestHeader(
                    name = "accept-language",
                    required = false) final String language) {
        MicroserviceResponse msResponse = new MicroserviceResponse();
        msResponse.setPath(appProperties.getBasepath());
        msResponse.setMethod(HttpMethod.POST);
        if (Validate.isEmpty(name)) {
            setErrorResponse(msResponse, HttpStatus.BAD_REQUEST,
                    "Missing input prameter");
            msResponse.addError("1000", MISSING_PARAMETER_ERROR_MSG);
            return msResponse;
        }
        if (!Validate.isValid(name)) {
            setErrorResponse(msResponse, HttpStatus.UNPROCESSABLE_ENTITY,
                    "Invalid input prameter");
            msResponse.addError("1001", INVALID_PARAMETER_ERROR_MSG);
            return msResponse;
        }
        Locale local = getLocale(language);
        try {
            String greet = service.sayHello(local.getISO3Language());
            Greeting greeting = new Greeting(counter.incrementAndGet(),
                    String.format(TEMPLATE, greet, name));
            msResponse.setHttpStatus(HttpStatus.OK);
            msResponse.setCode(HttpStatus.OK.value());
            msResponse.getData().set("greetings", Convertor.toNode(greeting));
            return msResponse;
        } catch (ValueNotFoundException ve) {
            setErrorResponse(msResponse, HttpStatus.NOT_FOUND, ve.getMessage());
            msResponse.addError("4040", ve.getMessage());
            return msResponse;
        } catch (Exception ex) {
            setErrorResponse(msResponse, HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getMessage());
            msResponse.addError("5000", ex.getMessage());
            return msResponse;

        }
    }
    /**
     * @return Locale
     */
    public Locale getLocale(final String language) {
        if (Validate.isEmpty(language)) {
            return Locale.getDefault();
        }
        return new Locale.Builder().setLanguage(language).build();

    }

    private void setErrorResponse(final MicroserviceResponse msResponse,
            final HttpStatus status, final String message) {
        msResponse.setHttpStatus(status);
        msResponse.setCode(status.value());
        msResponse.setMessage(message);
    }

}
