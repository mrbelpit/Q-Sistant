package accenture.demo.integration.admin;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import accenture.demo.admin.SpecialAppUserRegistrationDTO;
import accenture.demo.capacity.CapacityHandler;
import accenture.demo.capacity.CapacityInfoDTO;
import accenture.demo.capacity.CapacityModifier;
import accenture.demo.capacity.CapacitySetupDTO;
import accenture.demo.capacity.Message;
import accenture.demo.configuration.AppTestConfig;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.login.LoginResponseDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
public class AdminControllerIntegrationTest {

  private MockMvc mockMvc;
  private String tokenSmith;

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

    tokenSmith = registerLoginAndGetUsersToken("Smith", "smith", "smith@asd.com", "asd",
        "6");
  }

  @Test
  public void adminRegisterVip_expectOK_assertsEqual() throws Exception {
    MvcResult result = mockMvc.perform(post("/admin/user/register")
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith)
        .content(objectMapper
            .writeValueAsString(
                new SpecialAppUserRegistrationDTO("Tom", "Denem", "asd@gmail.com", "asd", "25",
                    UserRole.VIP))))
        .andExpect(status().isOk())
        .andReturn();

    AppUser vip = objectMapper
        .readValue(result.getResponse().getContentAsString(), AppUser.class);
    Assert.assertEquals("asd@gmail.com", vip.getEmail());
    Assert.assertEquals(UserRole.VIP, vip.getUserRole());
  }

  @Test
  public void adminRegisterVips_expectOK_assertsEqual() throws Exception {
    List<SpecialAppUserRegistrationDTO> vipList = new ArrayList<>();
    vipList.add(new SpecialAppUserRegistrationDTO("Tom", "Denem", "tom@gmail.com", "asd", "53",
        UserRole.VIP));
    vipList.add(new SpecialAppUserRegistrationDTO("Bob", "TheVip", "VipBob@gmail.com", "asd", "42",
        UserRole.VIP));

    MvcResult result = mockMvc.perform(post("/admin/users/register")
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith)
        .content(objectMapper.writeValueAsString(vipList)))
        .andExpect(status().isOk())
        .andReturn();

    List<AppUser> vipListResponse = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
    Assert.assertEquals(2, vipListResponse.size());
    Assert.assertEquals("tom@gmail.com", vipListResponse.get(0).getEmail());
    Assert.assertEquals("VipBob@gmail.com", vipListResponse.get(1).getEmail());
  }

  @Test
  public void getUsers_WithFilterVIP_assertEqual_expectOk() throws Exception {
    List<SpecialAppUserRegistrationDTO> vipList = new ArrayList<>();
    vipList.add(new SpecialAppUserRegistrationDTO("Tom", "Denem", "tom@gmail.com", "asd", "53",
        UserRole.ADMIN));
    vipList.add(new SpecialAppUserRegistrationDTO("Bob", "TheVip", "VipBob@gmail.com", "asd", "42",
        UserRole.VIP));
    vipList.add(new SpecialAppUserRegistrationDTO("Steve", "Just", "Steve@gmail.com", "asd", "22",
        UserRole.VIP));

    mockMvc.perform(post("/admin/users/register")
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith)
        .content(objectMapper.writeValueAsString(vipList)))
        .andExpect(status().isOk());

    String filter = "vip";

    MvcResult result = mockMvc.perform(get("/admin/users/" + filter)
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith))
        .andExpect(status().isOk())
        .andReturn();

    List<AppUser> userList = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
    Assert.assertEquals(2, userList.size());
  }

  @Test
  public void getUsers_WithFilterAll_assertEqual_expectOk() throws Exception {
    String filter = "all";

    MvcResult result = mockMvc.perform(get("/admin/users/" + filter)
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith))
        .andExpect(status().isOk())
        .andReturn();

    List<AppUser> userList = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
    Assert.assertEquals(1, userList.size());
  }

  @Test
  public void getUsers_WithFilterNull_assertEqual_expectBadRequest() throws Exception {

    String filter = null;

    MvcResult result = mockMvc.perform(get("/admin/users/" + filter)
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith))
        .andExpect(status().isBadRequest())
        .andReturn();

    String msg = result.getResponse().getContentAsString();
    Assert.assertEquals("User filter is not valid!", msg);
  }

  @Test
  public void getUsers_WithInvalidFilter_assertEqual_expectBadRequest() throws Exception {

    String filter = "asdasd";

    MvcResult result = mockMvc.perform(get("/admin/users/" + filter)
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith))
        .andExpect(status().isBadRequest())
        .andReturn();

    String msg = result.getResponse().getContentAsString();
    Assert.assertEquals("User filter is not valid!", msg);
  }


  @Test
  public void adminCalibrate_expectOK_assertsEqual() throws Exception {
    Integer percentage = 20;
    MvcResult result = mockMvc.perform(put("/admin/calibrate/headcount")
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSmith)
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
    MvcResult result = mockMvc.perform(get("/admin/info")
        .header("Authorization", "Bearer " + tokenSmith))
        .andExpect(status().isOk())
        .andReturn();

    CapacityInfoDTO capacityInfoDTO = objectMapper
        .readValue(result.getResponse().getContentAsString(), CapacityInfoDTO.class);

    Assert.assertEquals(Integer.valueOf(1), capacityInfoDTO.getFreeSpace());
    Assert.assertEquals(0, capacityInfoDTO.getWorkersInTheBuilding().size());
    Assert.assertEquals(Integer.valueOf(1), capacityInfoDTO.getMaxWorkerAllowedToEnter());
    Assert.assertEquals(Integer.valueOf(10), capacityInfoDTO.getWorkspaceCapacityPercentage());
    Assert.assertEquals(Integer.valueOf(10), capacityInfoDTO.getMaxWorkplaceSpace());
    Assert.assertEquals(Integer.valueOf(0), capacityInfoDTO.getWorkersCurrentlyInOffice());
  }

  private String registerLoginAndGetUsersToken(String firstName, String lastName, String email,
      String password, String cardId)
      throws Exception {
    mockMvc.perform(post("/register")
        .contentType(mediaType)
        .content(objectMapper.writeValueAsString(
            new RegistrationRequestDTO(firstName, lastName, email, password, cardId))))
        .andExpect(status().isOk())
        .andReturn();

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
