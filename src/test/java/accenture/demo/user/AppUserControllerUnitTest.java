package accenture.demo.user;


import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.security.CustomUserDetailService;
import accenture.demo.security.JwtUtility;
import accenture.demo.user.exceptionhandling.RegistrationExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
public class AppUserControllerUnitTest {

  private MockMvc mockMvc;

  ObjectMapper mapper = new ObjectMapper();

  @MockBean
  private UserService userService;
  @MockBean
  private CustomUserDetailService userDetailsService;
  @MockBean
  private JwtUtility jwtTokenUtil;

  @Before
  public void setup() {
    this.mockMvc = standaloneSetup(
            new UserController(userService, userDetailsService, jwtTokenUtil))
            .setControllerAdvice(new RegistrationExceptionHandler())
            .build();
  }

  @Test
  public void registerNewUser_success() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO(
            "Lajos", "The Mighty", "lajos@themightiest.com", "pw");
    AppUser testAppUser = new AppUser(1L, "Lajos", "The Mighty", "lajos@themightiest.com", "pw",
            "1");

    when(userService.createNewUser(any())).thenReturn(testAppUser);
    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("Lajos")))
            .andExpect(jsonPath("$.lastName", is("The Mighty")))
            .andExpect(jsonPath("$.email", is("lajos@themightiest.com")));
  }

  @Test
  public void login_successful() throws Exception {
    when(userService.validateLoginCredentials(any())).thenReturn(true);
    when(jwtTokenUtil.generateToken(any())).thenReturn("test@email.com");
    mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(
                    new LoginRequestDTO("test@email.com", "pw"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("status", is("ok")))
            .andExpect(jsonPath("token").exists())
            .andExpect(jsonPath("token").isNotEmpty())
            .andExpect(jsonPath("token").isString())
            .andExpect(jsonPath("token", is("test@email.com")))
            .andDo(print());
  }
}





