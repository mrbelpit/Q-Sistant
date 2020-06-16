package accenture.demo.unit.capacity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import accenture.demo.capacity.CapacityController;
import accenture.demo.capacity.CapacityExceptionHandler;
import accenture.demo.capacity.CapacityService;
import accenture.demo.capacity.Message;
import accenture.demo.configuration.AppTestConfig;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Import(AppTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
public class CapacityControllerUnitTest {

  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  private MediaType contentType;

  @MockBean
  private CapacityService capacityService;

  @MockBean
  private UserService userService;

  private AppUser testAppUser;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(
        new CapacityController(capacityService, userService))
        .setControllerAdvice(new CapacityExceptionHandler())
        .build();
    testAppUser = new AppUser(1L, "Lajos", "The Mighty", "lajos@themightiest.com", "pw",
        "1");
  }

  @Test
  @WithMockUser
  public void registerPlace_canEnterImmediately() throws Exception {
    Message message = new Message("You can enter the office!");
    when(capacityService.register(any())).thenReturn(message);
    MvcResult result = mockMvc.perform(post("/office/register")
        .contentType(contentType)
        .content(objectMapper.writeValueAsString(testAppUser)))
        .andExpect(status().isOk())
        .andReturn();

    Message responseMessage = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    assertEquals(responseMessage.getMessage(), message.getMessage());
  }

  @Test
  @WithMockUser
  public void registerPlace_placedInQueue() throws Exception {
    Message message = new Message("Your current place in the queue 1!");
    when(capacityService.register(any())).thenReturn(message);
    MvcResult result = mockMvc.perform(post("/office/register")
        .contentType(contentType)
        .content(objectMapper.writeValueAsString(testAppUser)))
        .andExpect(status().isOk())
        .andReturn();

    Message responseMessage = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    assertEquals(responseMessage.getMessage(), message.getMessage());
  }

  @Test
  @WithMockUser
  public void status_inQueue() throws Exception {
    Message message = new Message("Your current place in the queue is 1!");
    when(capacityService.currentStatus(any())).thenReturn(message);
    MvcResult result = mockMvc.perform(get("/office/status")
        .contentType(contentType)
        .content(objectMapper.writeValueAsString(testAppUser)))
        .andExpect(status().isOk())
        .andReturn();

    Message responseMessage = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    assertEquals(responseMessage.getMessage(), message.getMessage());
  }

  @Test
  @WithMockUser
  public void status_canEnter() throws Exception {
    Message message = new Message("You can enter the office today!");
    when(capacityService.currentStatus(any())).thenReturn(message);
    MvcResult result = mockMvc.perform(get("/office/status")
        .contentType(contentType)
        .content(objectMapper.writeValueAsString(testAppUser)))
        .andExpect(status().isOk())
        .andReturn();

    Message responseMessage = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    assertEquals(responseMessage.getMessage(), message.getMessage());
  }

  @Test
  @WithMockUser
  public void status_notApplied() throws Exception {
    Message message = new Message("You have not applied to enter the office today!");
    when(capacityService.currentStatus(any())).thenReturn(message);
    MvcResult result = mockMvc.perform(get("/office/status")
        .contentType(contentType)
        .content(objectMapper.writeValueAsString(testAppUser)))
        .andExpect(status().isOk())
        .andReturn();

    Message responseMessage = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    assertEquals(responseMessage.getMessage(), message.getMessage());
  }

  @Test
  public void entry_canEnter() throws Exception {
    Message message = new Message("Entry was successful!");
    when(capacityService.enterUser(any())).thenReturn(message);
    MvcResult result = mockMvc.perform(post("/office/entry/" + testAppUser.getCardId()))
        .andExpect(status().isOk())
        .andReturn();

    Message responseMessage = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    assertEquals(responseMessage.getMessage(), message.getMessage());
  }

  @Test
  public void entry_canNotEnter() throws Exception {
    when(capacityService.enterUser(any()))
        .thenThrow(new EntryDeniedException("User is currently not allowed to enter!"));
    mockMvc.perform(post("/office/entry/" + testAppUser.getCardId()))
        .andExpect(status().isForbidden());
  }

  @Test
  public void exit_canExit() throws Exception {
    Message message = new Message("Exit was successful!");
    when(capacityService.exitUser(any())).thenReturn(message);
    MvcResult result = mockMvc.perform(delete("/office/exit/" + testAppUser.getCardId()))
        .andExpect(status().isOk())
        .andReturn();

    Message responseMessage = objectMapper
        .readValue(result.getResponse().getContentAsString(), Message.class);
    assertEquals(responseMessage.getMessage(), message.getMessage());
  }
}
