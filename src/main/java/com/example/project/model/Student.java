// Copyright 2023 Kozlov Alexey

package com.example.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import java.sql.Date;

@Entity
@Table(name="studs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    private Integer idst;

    @Column(nullable = false)
    private String fn;

    @Column(nullable = false)
    private String ln;

    @Column(nullable = false)
    private Date dob;

    @Column(nullable = false)
    private Integer mentor;
}
