// Copyright 2023 Kozlov Alexey
package com.example.project;

import com.example.project.config.AppConfig;
import com.example.project.model.Student;
import com.example.project.repository.StudentRepository;
import com.example.project.service.StudentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = StudentService.class)
class ProjectApplicationTests {

    @MockBean
    private AppConfig appConfig;
    @MockBean
    private StudentRepository studentRepository;

    private static Student student;

    @Autowired
    private StudentService studentService;

    @Test
    void testInitStudent() {
        assertAll(
                ()->assertEquals(student.getIdst(), 1234),
                ()->assertEquals(student.getFn(), "alex"),
                ()->assertEquals(student.getLn(), "test"),
                ()->assertEquals(student.getDob(), Date.valueOf("2023-05-25")),
                ()->assertEquals(student.getMentor(), 1001)
        );
    }

    @Test
    void testCreateStudentInDb() {
        when(studentRepository.save(eq(student))).thenReturn(student);

        studentService.createStudent(student);

        verify(studentRepository, times(1)).save(student);
    }

    @BeforeAll
    public static void getRandomStudent() {
        student = new Student();
        student.setIdst(1234);
        student.setFn("alex");
        student.setLn("test");
        student.setDob(Date.valueOf("2023-05-25"));
        student.setMentor(1001);
    }

}
