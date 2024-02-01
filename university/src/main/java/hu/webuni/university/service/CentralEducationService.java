package hu.webuni.university.service;

import org.springframework.stereotype.Service;

import hu.webuni.eduservice.wsclient.SudentXmlWsImplService;
import hu.webuni.university.aspect.Retry;

@Service
public class CentralEducationService {

//	private Random random = new Random();

	@Retry(times = 5, waitTime = 500)
	public int getNumFreeSemestersForStudent(int eduId) {
		return new SudentXmlWsImplService().getSudentXmlWsImplPort().getFreeSemestersByStudent(eduId);
		
//		int rnd = random.nextInt(0, 2);
//		if (rnd == 0) {
//			throw new RuntimeException("Central Education Service timed out.");
//		} else {
//			return random.nextInt(0, 10);
//		}
	}

	public void askNumFreeSemestersForStudent(int eduId) {
		
	}
}
