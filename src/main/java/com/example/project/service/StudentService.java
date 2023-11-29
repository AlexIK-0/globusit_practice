// Copyright 2023 Kozlov Alexey

package com.example.project.service;

import com.example.project.config.AppConfig;
import com.example.project.model.Student;
import com.example.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    private AppConfig appConfig;

    private StudentRepository studentRepository;

    @Autowired
    public StudentService(AppConfig appConfig, StudentRepository studentRepository) {
        this.appConfig = appConfig;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public List<String> getAllStudent() {
        List<String> studentList = new ArrayList<>();
        studentRepository.findAll().forEach(model->studentList.add(model.toString()));
        return studentList;
    }

    @Transactional
    public Student getStudentById(Integer id) {
        return studentRepository.findById(id).get();
    }

    @Transactional
    public Student createStudent(Student student) {
        studentRepository.save(student);
        return student;
    }

}
