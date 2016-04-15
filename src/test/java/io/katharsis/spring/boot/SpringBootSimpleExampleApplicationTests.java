package io.katharsis.spring.boot;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.Charset;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootSimpleExampleApplication.class)
@WebIntegrationTest(randomPort = true)
@DirtiesContext
public class SpringBootSimpleExampleApplicationTests {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void testTestEndpointWithQueryParams() throws Exception {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        String uri = "http://localhost:" + this.port + "/api/tasks?filter[Task][name]=John";
        ResponseEntity<String> response = testRestTemplate.exchange(uri, HttpMethod.GET,
            new HttpEntity<>(createAuthHeaders("user", "password")), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThatJson(response.getBody()).node("data[0].attributes.name").isStringEqualTo("John");
        assertThatJson(response.getBody()).node("data[0].links.self").isStringEqualTo("http://localhost:8080/api/tasks/1");
    }

    @Test
    public void testTestCustomEndpoint() throws Exception {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<String> response = testRestTemplate
            .getForEntity("http://localhost:" + this.port + "/api/tasks/1", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), "hello");
    }

    HttpHeaders createAuthHeaders(final String username, final String password ){
        return new HttpHeaders(){
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("UTF-8")) );
                String authHeader = "Basic " + new String( encodedAuth );
                set( "Authorization", authHeader );
            }
        };
    }
}
