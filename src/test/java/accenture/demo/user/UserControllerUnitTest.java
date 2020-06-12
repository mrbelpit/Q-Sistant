package accenture.demo.user;


import accenture.demo.registration.RegistrationRequestDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
public class UserControllerUnitTest {

  private MockMvc mockMvc;

  ObjectMapper mapper = new ObjectMapper();

  @MockBean
  private UserService userService;


  @Before
  public void setup() {
    this.mockMvc = standaloneSetup(new UserController(userService)).build();

  }

  @Test
  public void registerNewUser_success() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO(
            "Lajos", "The Mighty", "lajos@themightiest.com", "pw");
    User testUser = new User(1L,"Lajos","The Mighty","lajos@themightiest.com","pw","1");

    when(userService.createNewUser(any())).thenReturn(testUser);
    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("Lajos")))
            .andExpect(jsonPath("$.lastName", is("The Mighty")))
            .andExpect(jsonPath("$.email", is("lajos@themightiest.com")));
  }
}





