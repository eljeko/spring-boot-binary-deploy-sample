package hello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
@PropertySource("file://${PROPERTIES_PATH}")
@RestController
public class HelloController {

    @Value("${externalgreetings}")
    private String externalgreetings;

    @RequestMapping("/")
    public String index() {
        return "Default: Greetings from Hello Service <br> External: " + externalgreetings;
    }

    @RequestMapping(value = "/hello",
            produces = {MediaType.APPLICATION_JSON_VALUE})

    public Hello hello() {
        return new Hello("Greetings from Hello Service", externalgreetings);
    }

}