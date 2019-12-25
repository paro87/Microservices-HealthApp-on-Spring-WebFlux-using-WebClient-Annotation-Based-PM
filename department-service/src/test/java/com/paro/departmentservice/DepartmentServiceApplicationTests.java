package com.paro.departmentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@PropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@SpringBootTest
class DepartmentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
