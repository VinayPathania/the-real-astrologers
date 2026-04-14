package com.astro.theRealAstrologers.Controller;

import com.astro.theRealAstrologers.Entity.BirthProfile;
import com.astro.theRealAstrologers.Entity.User;
import com.astro.theRealAstrologers.Repository.BirthProfileRepository;
import com.astro.theRealAstrologers.Repository.UserRepository;
import com.astro.theRealAstrologers.Service.AiPredictionService;
import com.astro.theRealAstrologers.Service.AstrologyService;
import com.astro.theRealAstrologers.Service.GeocodingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/profiles")
public class AstrologyController {

    @Autowired
    private BirthProfileRepository profileRepository;

    @Autowired
    private AstrologyService astrologyService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private AiPredictionService aiPredictionService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public String createProfileAndCalculate(@RequestBody BirthProfile profile) {

        // 1. Ask the Bouncer who is logged in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(loggedInEmail).orElse(null);

        if (currentUser != null) {
            profile.setUser(currentUser);
        } else {
            return "Error: Could not verify user identity.";
        }


         double[]  coords = geocodingService.getCoordinates(profile.getCityName());
        if (coords == null || coords.length < 2) {
            return "Error: The stars cannot locate the city of '" + profile.getCityName() + "'. Please check the spelling.";
        }
        profile.setLatitude(coords[0]);
        profile.setLongitude(coords[1]);

        // 2. Save to PostgreSQL (Now it has the user ID AND the coordinates!)
         profileRepository.save(profile);

        // 3. Run the Math (This will no longer crash because latitude is not null)
        Map<String, String> calculatedChart = astrologyService.calculateFullChart(profile);

        // 4. Generate AI Reading
        return aiPredictionService.generateReading(calculatedChart);
    }


    @GetMapping("/all")
    public List<BirthProfile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @GetMapping("/test")
    public String test() {
        return "Astrology API is live and running!";
    }
}
