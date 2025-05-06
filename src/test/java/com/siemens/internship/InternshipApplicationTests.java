package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InternshipApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getUrl(String path) {
        return "http://localhost:" + port + "/api/items" + path;
    }

    @Test
    void testCreateItemWithInvalidEmail() {
        String[] invalidEmails = {
                "plainaddress",
                "@missingusername.com",
                "username@.com",
                "username@com",
                "username@domain..com",
                "user@@domain.com",
                "user@domain.c"
        };

        for (String email : invalidEmails) {
            Item item = new Item();
            item.setName("InvalidEmail");
            item.setEmail(email);
            item.setDescription("Nothing");
            item.setStatus("ACTIVE");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Item> request = new HttpEntity<>(item, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(getUrl(""), request, String.class);

            assertThat(response.getStatusCode())
                    .withFailMessage("Expected 400 Bad Request for email: %s but got %s", email, response.getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void testCreateItemWithInvalidStatus() {
        String[] invalidStatuses = {
                "INVALID",
                "NOT_ACTIVE",
                "PENDING_STATUS",
                "ACTIVE_STATUS",
                "INACTIVE_STATUS"
        };
        for (String status : invalidStatuses) {
            Item item = new Item();
            item.setName("InvalidEmail");
            item.setEmail("email@yh.com");
            item.setDescription("Nothing");
            item.setStatus(status);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Item> request = new HttpEntity<>(item, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(getUrl(""), request, String.class);

            assertThat(response.getStatusCode())
                    .withFailMessage("Expected 400 Bad Request for status: %s but got %s", status, response.getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

}
