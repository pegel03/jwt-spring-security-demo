package org.zerhusen.rest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zerhusen.security.JwtTokenUtil;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonRestControllerTest {

    private MockMvc mvc;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
//    @AutoConfigureRestDocs(outputDir = "target/snippets")
    @WithMockUser(roles = "USER")
    public void shouldReturnSomeUsers() throws Exception {
        this.mvc.perform(get("/persons"))
//            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello")))
            .andDo(document("persons"));
    }


    @Test
    @WithMockUser(roles = "USER")
    public void shouldReturnAUsers() throws Exception {
        this.mvc.perform(get("/persons/{name}", "Hello")
            .accept(MediaType.APPLICATION_JSON))
//            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("World")))
            .andDo(document("persons/name", pathParameters(
                parameterWithName("name").description("The persons name")
            )));
    }

    @Test
    public void shouldGetUnauthorizedWithoutRole() throws Exception {

        this.mvc.perform(get("/persons"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getPersonsSuccessfullyWithUserRole() throws Exception {

        this.mvc.perform(get("/persons"))
            .andExpect(status().is2xxSuccessful());
    }

}

