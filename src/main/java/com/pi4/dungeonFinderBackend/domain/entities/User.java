package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class User {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private @Getter @Setter Long idUser;

    @Column(name = "USER", length = 12, nullable = false)
    private @Getter @Setter String user;

    @Column(name = "BIOGRAPHY", length = 150, nullable = true)
    private @Getter @Setter String biography;

    @Column(name = "CREATION_DATE", nullable = false)
    private @Getter @Setter LocalDateTime creationDate;

}
