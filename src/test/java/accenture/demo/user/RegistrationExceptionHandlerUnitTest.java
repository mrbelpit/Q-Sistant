package accenture.demo.user;


import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.exception.registration.RequestBodyIsNullException;
import accenture.demo.registration.RegistrationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
public class RegistrationExceptionHandlerUnitTest {

  private MockMvc mockMvc;

  ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private UserService userService;

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          StandardCharsets.UTF_8);


  @Before
  public void setup() {
    this.mockMvc = standaloneSetup(new UserController(userService))
            .setControllerAdvice(new RegistrationExceptionHandler())
            .build();
  }

  @Test
  public void missingParameterInRegistrationRequestDTO() throws Exception {
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO(null,
            "lastName", "email", "pw");

    MvcResult result = mockMvc.perform(post("/register")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andReturn();

    String expectedErrorMessage = "{\"firstName\": \"First name missing\"}";
    String actualResponseBody = result.getResponse().getContentAsString();
    assertThat(expectedErrorMessage)
            .isEqualToIgnoringWhitespace((CharSequence) actualResponseBody);
  }

  @Test
  public void requestBodyWithNullContentSentByUser() throws Exception {
    RegistrationRequestDTO testDTO = new RegistrationRequestDTO("lajos",
            "lastName", "email", "pw");
    RequestBodyIsNullException ex =
            new RequestBodyIsNullException(
                    "Please fill in the required fields");
    when(userService.createNewUser(any())).thenThrow(ex);
    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("status", is("error")))
            .andExpect(jsonPath("message", is(ex.getMessage())))
            .andDo(print());

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
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("status", is("error")))
            .andExpect(jsonPath("message", is(ex.getMessage())))
            .andDo(print());
  }

}
