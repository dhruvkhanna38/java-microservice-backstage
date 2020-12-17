package {{cookiecutter.group_id}}.example.service;

import org.springframework.stereotype.Service;

import {{cookiecutter.group_id}}.example.exceptions.UnexpectedValueException;
import {{cookiecutter.group_id}}.example.exceptions.ValueNotFoundException;
import {{cookiecutter.group_id}}.example.utils.Validate;

@Service
public class GreetingService {
    public String sayHello(final String languageCode)
            throws ValueNotFoundException {
        if (Validate.isEmpty(languageCode)) {
            return "Hi";
        }
        switch (languageCode.toLowerCase()) {
            case ("eng") :
            case ("en") :
                return "Hello";
            case ("jpn") :
                return "Kon'nichiwa";
            case ("fra") :
                throw new UnexpectedValueException(
                        languageCode.concat(" not permited"));
            case ("deu") :
                return "Hallo";
            default :
                throw new ValueNotFoundException(
                        languageCode.concat(" not found"));
        }
    }

}
