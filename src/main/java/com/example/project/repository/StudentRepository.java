// Copyright 2023 Kozlov Alexey

package com.example.project.repository;

import io.micrometer.core.annotation.Timed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.example.project.model.Student;

import java.util.Optional;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student, Integer> {
    @Timed("timedForDB")
    Optional<Student> queryAllByIdst(Integer idst);
}
