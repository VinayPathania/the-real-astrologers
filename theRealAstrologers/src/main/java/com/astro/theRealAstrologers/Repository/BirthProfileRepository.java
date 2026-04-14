package com.astro.theRealAstrologers.Repository;

import com.astro.theRealAstrologers.Entity.BirthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthProfileRepository extends JpaRepository<BirthProfile, Long> {

    List<BirthProfile> findByUserId(Long userId);
}
