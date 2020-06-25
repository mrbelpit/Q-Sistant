package accenture.demo.kafka;

import accenture.demo.capacity.CapacityService;
import accenture.demo.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageServiceImpl implements KafkaMessageService {

  @Autowired
  private KafkaTemplate<String, KafkaMessage> kafkaTemplate;

  private CapacityService capacityService;

  @Autowired
  public KafkaMessageServiceImpl(CapacityService capacityService) {
    this.capacityService = capacityService;
  }

  @Override
  public void sendMessageToUserByPlaceInQueue(int n) {
    AppUser user = capacityService.getNthUserInQueue(n);
    if (user != null) {
    String messageType = KafkaMessageType.NOTIFICATION_OF_ENTRY.getValue();
    String messageText = "Dear " + user.getFirstName() + " (" + user.getLastName() + " )Your place "
                         + "in the queue is: " + n
                         + ". You will be allowed to enter the office soon.";
      kafkaTemplate.send(messageType, new KafkaMessage(messageType, messageText));
    }
  }
}
