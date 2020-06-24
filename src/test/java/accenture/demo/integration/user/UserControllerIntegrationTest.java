package accenture.demo.integration.user;

import accenture.demo.configuration.AppTestConfig;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.registration.RegistrationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(AppTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EnableWebMvc
public class UserControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private MediaType mediaType;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    RegistrationRequestDTO ramboRegistrationRequestDTO;

    @Before
    public void before() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        ramboRegistrationRequestDTO = new RegistrationRequestDTO("John", "Bambo", "johnbambo@jungle.man", "asd", "1");
    }

    @Test
    public void register_successful_assertEquals() throws Exception {
        MvcResult result = mockMvc.perform(post("/register")
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(ramboRegistrationRequestDTO)))
                .andExpect(status()
                        .isOk())
                .andReturn();
        RegistrationResponseDTO resultDTO = objectMapper
                .readValue(result.getResponse().getContentAsString(), RegistrationResponseDTO.class);
        assertEquals(Long.valueOf(1L), resultDTO.getId());
        assertEquals("johnbambo@jungle.man", resultDTO.getEmail());
    }

    @Test
    public void register_unsuccessful_emailAlreadyExists() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(ramboRegistrationRequestDTO)))
                .andExpect(status()
                .isOk())
                .andReturn();
        mockMvc.perform(post("/register")
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(ramboRegistrationRequestDTO)))
                .andExpect(status()
                .isBadRequest())
                .andReturn();
    }

    @Test
    public void login_successful() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(ramboRegistrationRequestDTO)));
        LoginRequestDTO request = new LoginRequestDTO("johnbambo@jungle.man", "asd");
        mockMvc.perform(post("/login")
                .contentType(mediaType)
                .content(
                        objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void login_noSuchUser() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("nosuch@user.com", "imfakeanyway");
        mockMvc.perform(post("/login")
                .contentType(mediaType)
                .content(
                        objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_wrongPassword() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(ramboRegistrationRequestDTO)));

        LoginRequestDTO request = new LoginRequestDTO("johnbambo@jungle.man", "isthismypassword");
        mockMvc.perform(post("/login")
                .contentType(mediaType)
                .content(
                        objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

}
