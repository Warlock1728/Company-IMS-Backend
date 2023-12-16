package com.bytesfarms.companyims;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bytesfarms.companyMain.CompanyImsApplication;

@SpringBootTest(classes = CompanyImsApplication.class)
@ExtendWith(SpringExtension.class)
public class CompanyImsApplicationTests {

    @Test
    public void contextLoads() {
        // Your test logic goes here
    }
}
