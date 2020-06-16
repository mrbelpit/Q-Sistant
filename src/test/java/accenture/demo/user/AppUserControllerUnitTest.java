package accenture.demo.user;


import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import accenture.demo.configuration.AppTestConfig;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.registration.RegistrationResponseDTO;
import accenture.demo.security.CustomUserDetailService;
import accenture.demo.security.JwtUtility;
import accenture.demo.user.exceptionhandling.RegistrationExceptionHandler;
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

@Import(AppTestConfig.class)
@RunWith(SpringRunner.class)
public class AppUserControllerUnitTest {

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
    this.mockMvc = standaloneSetup(
        new UserController(userService, userDetailsService, jwtTokenUtil, modelMapper))
        .setControllerAdvice(new RegistrationExceptionHandler())
        .build();
  }

  @Test
  public void registerNewUser_success() throws Exception {
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO(
        "Lajos", "The Mighty", "lajos@themightiest.com", "pw");
    AppUser testAppUser = new AppUser(1L, "Lajos", "The Mighty", "lajos@themightiest.com", "pw",
        "1");
    RegistrationResponseDTO registrationResponseDTO = new RegistrationResponseDTO(1L, "Lajos",
        "The Mighty", "lajos@themightiest.com", null, null);
    when(userService.createNewUser(any())).thenReturn(testAppUser);
    when(modelMapper.map(testAppUser, RegistrationResponseDTO.class))
        .thenReturn(registrationResponseDTO);
    System.out.println(testAppUser.getId());
    mockMvc.perform(post("/register")
        .contentType(contentType)
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
        .contentType(contentType)
        .content(objectMapper.writeValueAsString(
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





