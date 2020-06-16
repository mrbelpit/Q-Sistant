package accenture.demo.unit.user.exceptionhandling;

import accenture.demo.configuration.AppTestConfig;
import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.login.WrongPasswordException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.security.CustomUserDetailService;
import accenture.demo.security.JwtUtility;
import accenture.demo.user.UserController;
import accenture.demo.user.UserService;
import accenture.demo.user.exceptionhandling.LoginExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Import(AppTestConfig.class)
@RunWith(SpringRunner.class)
public class LoginExceptionHandlerUnitTest {


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
  @MockBean
  private ModelMapper modelMapper;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, userDetailsService, jwtTokenUtil, modelMapper))
            .setControllerAdvice(new LoginExceptionHandler())
            .build();
  }

  @Test
  public void handleNoSuchUserException() throws Exception {
    LoginRequestDTO loginRequestDTO = new LoginRequestDTO("GeneralKenobi", "Hello, there");
    NoSuchUserException e = new NoSuchUserException(
            "This email address in not registered");
    when(userService.validateLoginCredentials(any())).thenThrow(e);
    mockMvc.perform(post("/login")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(loginRequestDTO)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("status", is("error")))
            .andExpect(jsonPath("message", is(e.getMessage())))
            .andDo(print());
  }

  @Test
  public void handleWrongPasswordException() throws Exception {
    LoginRequestDTO user = new LoginRequestDTO("GeneralKenobi", "typo");
    WrongPasswordException e = new WrongPasswordException(
            "Wrong password!");
    when(userService.validateLoginCredentials(any())).thenThrow(e);
    mockMvc.perform(post("/login")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("status", is("error")));
  }
}
