package accenture.demo.integration.capacity;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import accenture.demo.capacity.CapacityHandler;
import accenture.demo.capacity.Message;
import accenture.demo.configuration.AppTestConfig;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.login.LoginResponseDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(AppTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@EnableWebMvc
public class CapacityControllerIntegrationTest {

  private MockMvc mockMvc;
  private String tokenSteve;
  private String tokenBob;

  @Autowired
  private MediaType mediaType;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @Before
  public void before() throws Exception {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(10);
    CapacityHandler.getInstance().setWorkspaceCapacity(10);
    CapacityHandler.getInstance().restartDay();
    CapacityHandler.getInstance().setUsersCurrentlyInOffice(new ArrayList<>());

    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();

    tokenSteve = registerLoginAndGetUsersToken("Steve", "Strong", "strong.steve@asd.com", "asd",
        "1");
    tokenBob = registerLoginAndGetUsersToken("Bob", "Silent", "silent.bob@asd.com", "asd", "2");
  }

  @Test
  public void officeStatusAppUser_expectOK_assertsEqual() throws Exception {
    System.out.println(CapacityHandler.getInstance().getAllowedUsers().remainingCapacity());
    Assert.assertEquals("You can enter the office!", officeRegister(tokenSteve).getMessage());
    System.out.println(CapacityHandler.getInstance().getAllowedUsers().remainingCapacity());
    MvcResult result = mockMvc.perform(post("/office/register")
        .header("Authorization", "Bearer " + tokenBob))
        .andExpect(status().isOk())
        .andReturn();

    System.out.println(CapacityHandler.getInstance().getAllowedUsers().remainingCapacity());
    Message message = objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);
    Assert.assertEquals("Your current place in the queue 1!", message.getMessage());
  }

  @Test
  public void officeRegisterAppUser_expectOK_assertsEqual() throws Exception {
    Assert.assertEquals("You can enter the office!", officeRegister(tokenSteve).getMessage());
  }

  @Test
  public void officeEntry_expectOK_assertsEqual() throws Exception {
    MvcResult result = mockMvc.perform(post("/office/entry/" + 1))
        .andExpect(status().isOk())
        .andReturn();

    Message message = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    Assert.assertEquals("Entry was successful!", message.getMessage());
  }

  @Test
  public void exitEntry_expectOK_assertsEqual() throws Exception {
    mockMvc.perform(post("/office/entry/" + 1))
        .andExpect(status().isOk());

    MvcResult result = mockMvc.perform(delete("/office/exit/" + 1))
        .andExpect(status().isOk())
        .andReturn();

    Message message = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    Assert.assertEquals("Exit was successful!", message.getMessage());
  }

  private Message officeRegister(String token) throws Exception {
    MvcResult result = mockMvc.perform(post("/office/register")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();

    return objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);
  }

  private String registerLoginAndGetUsersToken(String firstName, String lastName, String email,
      String password, String cardId)
      throws Exception {

    mockMvc.perform(post("/register")
        .contentType(mediaType)
        .content(objectMapper.writeValueAsString(
            new RegistrationRequestDTO(firstName, lastName, email, password, cardId))))
        .andExpect(status().isOk());


    MvcResult result = mockMvc.perform(post("/login")
        .contentType(mediaType)
        .content(
            objectMapper.writeValueAsString(new LoginRequestDTO(email, password))))
        .andExpect(status().isOk())
        .andReturn();

    return objectMapper
        .readValue(result.getResponse().getContentAsString(), LoginResponseDTO.class)
        .getToken();
  }
}
