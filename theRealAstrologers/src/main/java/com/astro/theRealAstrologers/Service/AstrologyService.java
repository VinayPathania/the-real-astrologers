package com.astro.theRealAstrologers.Service;

import com.astro.theRealAstrologers.Entity.BirthProfile;
import org.springframework.stereotype.Service;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

import java.util.HashMap;
import java.util.Map;

@Service
public class AstrologyService {

    private SwissEph sw;

    public AstrologyService() {
        this.sw = new SwissEph();
    }

    public String calculateBasicChart(BirthProfile profile) {
        // 2. Convert standard date to Julian Day (Astronomy Time)
        double julianDay = getJulianDay(profile);

        // 3. Array to hold the results [longitude, latitude, distance, etc.]
        double[] results = new double[6];
        StringBuffer error = new StringBuffer();

        // 4. Calculate the Sun's exact degree
        // SE_SUN = The Sun
        // SEFLG_MOSEPH = Use built-in math formulas so we don't need external files right now
        // SEFLG_SIDEREAL = Use Vedic (Nirayana) fixed star astrology
        int flag = SweConst.SEFLG_MOSEPH | SweConst.SEFLG_SIDEREAL;

        sw.swe_calc_ut(julianDay, SweConst.SE_SUN, flag, results, error);

        double sunDegree = results[0]; // This is a number between 0 and 360

        // 5. Convert the degree to a Sign
        String zodiacSign = getZodiacSign(sunDegree);

        return "Success! For " + profile.getPersonName() +
                ", the Sun was at " + String.format("%.2f", sunDegree) +
                " degrees. This means their Vedic Sun Sign is: " + zodiacSign;
    }

    public Map<String, String> calculateFullChart(BirthProfile profile) {
        // We will store the results in a Map (Key = Planet Name, Value = Zodiac Sign)
        Map<String, String> chartResults = new HashMap<>();

        // 1. Convert Time to Julian Day
        double julianDay = getJulianDay(profile);

        // 2. Set the Ayanamsa (Lahiri is the standard for Vedic Astrology)
        sw.swe_set_sid_mode(SweConst.SE_SIDM_LAHIRI, 0, 0);

        // 3. Flags: Use Sidereal (Vedic) + Built-in math
        int flags = SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_MOSEPH;

        // --- CALCULATE PLANETS ---
        // Array of planets we want to calculate
        int[] planets = {
                SweConst.SE_SUN, SweConst.SE_MOON, SweConst.SE_MARS,
                SweConst.SE_MERCURY, SweConst.SE_JUPITER, SweConst.SE_VENUS,
                SweConst.SE_SATURN, SweConst.SE_TRUE_NODE // True Node = Rahu
        };

        String[] planetNames = {
                "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn", "Rahu"
        };

        double[] results = new double[6];
        StringBuffer error = new StringBuffer();

        for (int i = 0; i < planets.length; i++) {
            sw.swe_calc_ut(julianDay, planets[i], flags, results, error);
            double degree = results[0];
            chartResults.put(planetNames[i], getZodiacSign(degree));

            // Ketu is exactly 180 degrees opposite of Rahu
            if (planets[i] == SweConst.SE_TRUE_NODE) {
                double ketuDegree = (degree + 180.0) % 360.0;
                chartResults.put("Ketu", getZodiacSign(ketuDegree));
            }
        }

        // --- CALCULATE ASCENDANT (LAGNA) ---
        // The Ascendant requires latitude and longitude!
        double[] cusps = new double[13];
        double[] ascmc = new double[10];

        // 'P' stands for Placidus house system (standard)
        sw.swe_houses(julianDay, flags, profile.getLatitude(), profile.getLongitude(), 'P', cusps, ascmc);

        // ascmc[0] contains the exact degree of the Ascendant
        double ascendantDegree = ascmc[0];
        chartResults.put("Ascendant", getZodiacSign(ascendantDegree));

        return chartResults;
    }



    // Helper Method 1: Date Converter
    private double getJulianDay(BirthProfile profile) {
        // Converting IST (Indian Standard Time) to UT (Universal Time)
        // Subtract 5 hours and 30 minutes from the birth time
        double birthHourUT = profile.getBirthTime().getHour() - 5.5 + (profile.getBirthTime().getMinute() / 60.0);

        SweDate sd = new SweDate(
                profile.getBirthDate().getYear(),
                profile.getBirthDate().getMonthValue(),
                profile.getBirthDate().getDayOfMonth(),
                birthHourUT
        );
        return sd.getJulDay();
    }

    private String getZodiacSign(double degree) {
        String[] signs = {"Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
                "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"};
        int signIndex = (int) (degree / 30);
        return signs[signIndex];
    }
}
