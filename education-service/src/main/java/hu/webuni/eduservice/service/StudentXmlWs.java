package hu.webuni.eduservice.service;

import jakarta.jws.WebService;

@WebService
public interface StudentXmlWs {

	int getFreeSemestersByStudent(int eduId);

}