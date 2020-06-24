package accenture.demo.integration.admin;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import accenture.demo.admin.SpecialAppUserRegistrationDTO;
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
  private String tokenSteve;

  @Autowired
  private MediaType mediaType;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @Before
  public void before() throws Exception {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();

    tokenSteve = registerLoginAndGetUsersToken("Steve", "Strong", "strong.steve@asd.com", "asd",
        "1");
  }

  @Test
  public void adminRegisterVip_expectOK_assertsEqual() throws Exception {
    MvcResult result = mockMvc.perform(post("/admin/user/register")
        .contentType(mediaType)
        .header("Authorization", "Bearer " + tokenSteve)
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
        .header("Authorization", "Bearer " + tokenSteve)
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
