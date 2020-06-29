package accenture.demo.unit.capacity.exceptionhandling;


import accenture.demo.admin.AdminController;
import accenture.demo.capacity.CapacityController;
import accenture.demo.capacity.CapacityExceptionHandler;
import accenture.demo.capacity.CapacityService;
import accenture.demo.capacity.CapacitySetupDTO;
import accenture.demo.configuration.AppTestConfig;
import accenture.demo.distance.DistanceService;
import accenture.demo.exception.appuser.CardIdNotExistException;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.capacity.InvalidCapacitySetupModifierException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.kafka.KafkaMessageService;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import accenture.demo.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static accenture.demo.capacity.CapacityModifier.WORKPLACE_SPACE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(AppTestConfig.class)
@RunWith(SpringRunner.class)
public class CapacityExceptionHandlerUnitTest {

  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  private MediaType contentType;

  @MockBean
  UserService userService;
  @MockBean
  CapacityService capacityService;
  @MockBean
  DistanceService distanceService;
  @MockBean
  KafkaMessageService kafkaMessageService;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(userService,
                    capacityService, distanceService),
            new CapacityController(capacityService, userService,kafkaMessageService))
            .setControllerAdvice(new CapacityExceptionHandler())
            .build();
  }


  @Test
  public void capacitySetupDTO_isNull_handleCapacitySetupException() throws Exception {
    CapacitySetupDTO capacitySetupDTO = null;
    CapacitySetupException e = new CapacitySetupException("CapacitySetupDTO can not be null!");
    when(capacityService.capacitySetup(any())).thenThrow(e);
    mockMvc.perform(put("/admin/calibrate/distance")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(capacitySetupDTO)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  public void capacitySetupDTO_modifierIsNull_handleInvalidCapacitySetupModifierException()
          throws Exception {
    CapacitySetupDTO capacitySetupDTO = new CapacitySetupDTO(null, 2);
    InvalidCapacitySetupModifierException e = new InvalidCapacitySetupModifierException(
            "The provided modifier can not be null!");
    when(capacityService.capacitySetup(any())).thenThrow(e);
    mockMvc.perform(put("/admin/calibrate/headcount")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(capacitySetupDTO)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  public void capacitySetupDTO_valueIsNull_handleInvalidCapacitySetupModifierException()
          throws Exception {
    CapacitySetupDTO capacitySetupDTO = new CapacitySetupDTO(WORKPLACE_SPACE, null);
    InvalidCapacitySetupModifierException e = new InvalidCapacitySetupModifierException(
            "The provided modifier can not be null!");
    when(capacityService.capacitySetup(any())).thenThrow(e);
    mockMvc.perform(put("/admin/calibrate/headcount")
            .contentType(contentType)
            .content(objectMapper.writeValueAsString(capacitySetupDTO)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  public void officeEntry_cardIdDoesNotExists() throws Exception {
    when(capacityService.enterUser(any()))
            .thenThrow(new CardIdNotExistException("The provided card ID is not valid!"));
    mockMvc.perform(post("/office/entry/1"))
            .andExpect(status().isOk());
  }
}
