package hu.webuni.eduservice.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SudentXmlWsImpl implements StudentXmlWs {

	private Random random = new Random();
	
	@Override
	public int getFreeSemestersByStudent(int eduId) {
		return random.nextInt(0, 10);		
	}
}
