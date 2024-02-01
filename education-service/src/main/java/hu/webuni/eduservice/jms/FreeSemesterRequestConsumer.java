package hu.webuni.eduservice.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import hu.webuni.eduservice.service.StudentXmlWs;
import hu.webuni.jms.dto.FreeSemesterRequest;
import hu.webuni.jms.dto.FreeSemesterResponse;
import jakarta.jms.Topic;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FreeSemesterRequestConsumer {

	private final JmsTemplate jmsTemplate;
	private final StudentXmlWs studentXmlWs;
	
	@JmsListener(destination = "free_semester_requests")
	public void onFreeSemesterRequest(Message<FreeSemesterRequest> message) {
		int studentId = message.getPayload().getStudentId();
		int freeSemesters = studentXmlWs.getFreeSemestersByStudent(studentId);
		
		FreeSemesterResponse freeSemesterResponse = new FreeSemesterResponse();
		freeSemesterResponse.setStudentId(studentId);
		freeSemesterResponse.setNumFreeSemesters(freeSemesters);
		
		jmsTemplate.convertAndSend((Topic)message.getHeaders().get(JmsHeaders.REPLY_TO), freeSemesterResponse);
	}
}
