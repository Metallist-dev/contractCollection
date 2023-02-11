package de.metallist.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.testng.Assert.assertNotEquals;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private MainController controller;

    @Test
    void contextLoads() {
        assertNotEquals(controller, null);
    }

}
