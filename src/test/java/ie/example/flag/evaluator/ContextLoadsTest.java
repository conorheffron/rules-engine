package ie.example.flag.evaluator;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.example.flag.evaluator.controller.FlagController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = ApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Profile( "test")
class ContextLoadsTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlagController flagController;

    @Test
    void contextLoads() {
        // If the application context fails to start, this test fails before it runs.
        assertTrue(true);
    }

    @Test
    void controllerBean_isPresent() {
        assertNotNull(flagController);
    }
}