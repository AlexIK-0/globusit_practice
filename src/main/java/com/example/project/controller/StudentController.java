// Copyright 2023 Kozlov Alexey

package com.example.project.controller;

import com.example.project.model.Student;
import com.example.project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class StudentController {

    private StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String StartPage() {
        return "Successful";
    }

    @GetMapping("/students/allStudents")
    public String GetAllStudent() {
        return studentService.getAllStudent().toString();
    }

    @GetMapping("/students/{id}")
    public String GetStudentById(@PathVariable("id") Integer id) {
        return studentService.getStudentById(id).toString();
    }

    @PostMapping("students/create")
    public String createStudent(Student student) {
        return studentService.createStudent(student).toString();
    }

}
