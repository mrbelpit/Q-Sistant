package accenture.demo.integration.capacity;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import accenture.demo.capacity.CapacityHandler;
import accenture.demo.capacity.CapacityInfoDTO;
import accenture.demo.capacity.CapacityModifier;
import accenture.demo.capacity.CapacitySetupDTO;
import accenture.demo.capacity.Message;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.login.LoginResponseDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.registration.RegistrationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@EnableWebMvc
public class CapacityControllerIntegrationTest {

  private MockMvc mockMvc;
  private String tokenSteve;
  private String tokenBob;


  private MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      StandardCharsets.UTF_8);

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @Before
  public void before() throws Exception {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(10);
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
  public void officeRegisterAppUser_expectOK_assertsEqual() throws Exception {
    Assert.assertEquals("You can enter the office!", officeRegister(tokenSteve).getMessage());
  }

  @Test
  public void officeStatusAppUser_expectOK_assertsEqual() throws Exception {
    officeRegister(tokenSteve);
    String message = officeRegister(tokenBob).getMessage();
    Assert.assertEquals("Your current place in the queue 1!", message);
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

  @Test
  public void adminCalibrate_expectOK_assertsEqual() throws Exception {
    Integer percentage = 20;
    MvcResult result = mockMvc.perform(put("/office/admin/calibrate")
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenBob)
        .content(objectMapper
            .writeValueAsString(
                new CapacitySetupDTO(CapacityModifier.WORKSPACE_CAPACITY, percentage))))
        .andExpect(status().isOk())
        .andReturn();

    Message message = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    String expectedMsg = "The max workplace capacity successfully set to " + percentage
        + ". It is valid from now.";
    Assert.assertEquals(expectedMsg, message.getMessage());
  }

  @Test
  public void adminInfo_expectOK_assertsEqual() throws Exception {
    MvcResult result = mockMvc.perform(get("/office/admin/info")
        .header("Authorization", "Bearer " + tokenBob))
        .andExpect(status().isOk())
        .andReturn();

    CapacityInfoDTO capacityInfoDTO = objectMapper
        .readValue(result.getResponse().getContentAsString(), CapacityInfoDTO.class);

    Assert.assertEquals(Integer.valueOf(1),capacityInfoDTO.getFreeSpace());
    Assert.assertEquals(0,capacityInfoDTO.getWorkersInTheBuilding().size());
    Assert.assertEquals(Integer.valueOf(1),capacityInfoDTO.getMaxWorkerAllowedToEnter());
    Assert.assertEquals(Integer.valueOf(10),capacityInfoDTO.getWorkspaceCapacityPercentage());
    Assert.assertEquals(Integer.valueOf(10),capacityInfoDTO.getMaxWorkplaceSpace());
    Assert.assertEquals(Integer.valueOf(0),capacityInfoDTO.getWorkersCurrentlyInOffice());
  }

  private Message officeRegister(String token) throws Exception {
    MvcResult result = mockMvc.perform(post("/office/register")
        .contentType(mediaType)
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();

    return objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);
  }

  private String registerLoginAndGetUsersToken(String firstName, String lastName, String email,
      String password, String cardId)
      throws Exception {
    MvcResult result01 = mockMvc.perform(post("/register")
        .contentType(mediaType)
        .content(objectMapper.writeValueAsString(
            new RegistrationRequestDTO(firstName, lastName, email, password, cardId))))
        .andExpect(status().isOk())
        .andReturn();
    System.out.println(objectMapper
        .readValue(result01.getResponse().getContentAsString(), RegistrationResponseDTO.class)
        .getId());

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
