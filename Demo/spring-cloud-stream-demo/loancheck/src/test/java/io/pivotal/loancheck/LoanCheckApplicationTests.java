package io.pivotal.loancheck;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoanCheckApplicationTests {

  @Autowired
  private LoanProcessor processor;

  @Autowired
  private MessageCollector messageCollector;

  @Test
  public void testSendingAndReceivingOneMessage() throws JsonProcessingException, NullPointerException {
    Loan loan = new Loan("6bd17dd0-e942-4763-bd42-1e8612387eec", "Achiel Vandevivere", 500);

    processor.sourceOfLoanApplications().send(message(loan));
    Message<?> received =  messageCollector.forChannel(processor.approved()).poll();
    System.out.println(received);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(loan);
    assert received != null;
    assertThat(json).isEqualTo(received.getPayload());
  }

  private static <T> Message<T> message(T val) {
    return MessageBuilder.withPayload(val).build();
  }

}
