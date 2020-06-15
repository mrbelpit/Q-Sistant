package accenture.demo.user.exceptionhandling;


import accenture.demo.configuration.AppTestConfig;
import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.security.CustomUserDetailService;
import accenture.demo.security.JwtUtility;
import accenture.demo.user.UserController;
import accenture.demo.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(AppTestConfig.class)
@RunWith(SpringRunner.class)
public class ReceivedDTOValidationExceptionHandlerTest {

  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  private MediaType contentType;

  @MockBean
  private UserService userService;
  @MockBean
  private CustomUserDetailService userDetailsService;
  @MockBean
  private JwtUtility jwtTokenUtil;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(
            new UserController(userService, userDetailsService, jwtTokenUtil))
            .setControllerAdvice(new ReceivedDTOValidationExceptionHandler())
            .build();
  }


  @Test
  public void registerFail_requestBodyWithNullContentSentByUser() throws Exception {
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO("nulldto",
            "lastName", "email", "pw");
    RequestBodyIsNullException ex =
            new RequestBodyIsNullException(
                    "Please fill in the required fields");
    when(userService.createNewUser(any())).thenThrow(ex);
    mockMvc.perform(post("/register")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("status", is("error")))
            .andExpect(jsonPath("message", is(ex.getMessage())))
            .andDo(print());

  }

  @Test
  public void signUp_missingParameterInRegistrationRequestDTO() throws Exception {
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO(null,
            "lastName", "email", "pw");
    Map<String, String> errorMessage =  new HashMap<>();
    errorMessage.put("firstName", "First name missing");

    MvcResult result = mockMvc.perform(post("/register")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andReturn();

    String expectedErrorMessage = objectMapper.writeValueAsString(errorMessage);
    String actualResponseBody = result.getResponse().getContentAsString();
    assertThat(expectedErrorMessage)
            .isEqualToIgnoringWhitespace((CharSequence) actualResponseBody);
  }

  @Test
  public void loginFail_requestBodyWithNullContentSentByUser() throws Exception {
    LoginRequestDTO testDTO = new LoginRequestDTO("nulldto",
            "pw");
    RequestBodyIsNullException ex =
            new RequestBodyIsNullException(
                    "Please fill in the required fields");
    when(userService.validateLoginCredentials(any())).thenThrow(ex);
    mockMvc.perform(post("/login")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("status", is("error")))
            .andExpect(jsonPath("message", is(ex.getMessage())))
            .andDo(print());
  }

  @Test
  public void loginFail_missingParameterInRegistrationRequestDTO() throws Exception {
    LoginRequestDTO testDTO = new LoginRequestDTO(null, "pw");
    Map<String, String> errorMessage =  new HashMap<>();
    errorMessage.put("email", "Email address is missing");

    MvcResult result = mockMvc.perform(post("/login")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andReturn();

    String expectedErrorMessage = objectMapper.writeValueAsString(errorMessage);
    String actualResponseBody = result.getResponse().getContentAsString();
    assertThat(expectedErrorMessage)
            .isEqualToIgnoringWhitespace((CharSequence) actualResponseBody);
  }
}
