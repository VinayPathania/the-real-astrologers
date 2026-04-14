package com.astro.theRealAstrologers.Repository;

import com.astro.theRealAstrologers.Entity.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, Long> {
    // Spring generates the SQL to find the prompt by its name
    PromptTemplate findByTemplateName(String templateName);
}
