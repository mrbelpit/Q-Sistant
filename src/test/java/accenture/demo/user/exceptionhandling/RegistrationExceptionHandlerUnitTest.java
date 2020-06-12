package accenture.demo.user.exceptionhandling;


import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.security.CustomUserDetailService;
import accenture.demo.security.JwtUtility;
import accenture.demo.user.UserController;
import accenture.demo.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class RegistrationExceptionHandlerUnitTest {

  private MockMvc mockMvc;

  ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private UserService userService;
  @MockBean
  private CustomUserDetailService userDetailsService;
  @MockBean
  private JwtUtility jwtTokenUtil;

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          StandardCharsets.UTF_8);


  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(
            new UserController(userService, userDetailsService, jwtTokenUtil))
            .setControllerAdvice(new RegistrationExceptionHandler())
            .build();
  }

  @Test
  public void emailAlreadyRegistered() throws Exception {
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO("lajos",
            "lastName", "email", "pw");
    EmailAddressIsAlreadyRegisteredException ex =
            new EmailAddressIsAlreadyRegisteredException(
                    "This email address is already registered");
    when(userService.createNewUser(any())).thenThrow(ex);
    mockMvc.perform(post("/register")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("status", is("error")))
            .andExpect(jsonPath("message", is(ex.getMessage())))
            .andDo(print());
  }
}