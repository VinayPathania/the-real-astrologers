package com.astro.theRealAstrologers.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "prompt_templates")
@Data
public class PromptTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String templateName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String templateText;
}
