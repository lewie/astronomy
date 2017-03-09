package de.lewie.astronomy.test;
/**
*
* Copyright (C) 2010-2017, Lewi Cleantech GmbH <info@lewi-cleantech.de>
*
* @author Helmut Lehmeyer
* @date 09.03.2012
* @version 0.1
*/

import static de.lewie.astronomy.util.Astronomy.*;
import static de.lewie.astronomy.util.LewieMath.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.lewie.astronomy.util.Astronomy.Coor;
import de.lewie.astronomy.util.Astronomy.Riseset;

public class AstronomyTest {

    private static Calendar current_time;
    private static Calendar local_time;
    private static int year;
    private static int month;
    private static int day;
    private static int hour;
    private static int minute;
    private static int second;
    private static int localgmtdiff;
    private static boolean test;

    public AstronomyTest(double lat, double lon, boolean t) {
        test = t;
        main(lat, lon, new GregorianCalendar(), 0, 0, 0);
    }

    public AstronomyTest(double lat, double lon) {
        main(lat, lon, new GregorianCalendar(), 0, 0, 0);
    }

    public static void main(double lat, double lon) {
        main(lat, lon, new GregorianCalendar(), 0, 0, 0);
    }

    public static void main(double lat, double lon, GregorianCalendar daTi) {
        main(lat, lon, daTi, 0, 0, 0);
    }

    public static void main(double la, double lo, GregorianCalendar daTi, int zo, int dH, int dT) {

        // possible Inputs
        final double latitude = la >= -90 && la <= 90 ? la : 48.21;
        final double longitude = lo >= 0 && lo <= 180 ? lo : 16.37;
        final GregorianCalendar dateTime = daTi != null ? daTi : new GregorianCalendar(); // TODO!!!!!!!
        localgmtdiff = zo >= 0 && zo <= 12 ? zo : 0; // Zone
        final int deltaH = dH >= 0 && dH <= 12000 ? dH : 0; // Height above sea level in Meter
        final double deltaT = dT >= 50 && dT <= 70 ? dT : 65; // deltaT - difference among 'earth center' versus
                                                              // 'observered' time (TDT-UT), in seconds

        // current Time //Todo:, external date
        if (test) {
            currentTimeAndDateTEST();
        } else {
            currentTimeAndDate("Europe/Berlin");
        }

        if (year <= 1900 || year >= 2100) {
            System.out.println("Dies Script erlaubt nur Berechnungen"
                    + "in der Zeitperiode 1901-2099. Angezeigte Resultat sind ungültig.");
            return;
        }

        // Formating options:
        // http://openbook.galileodesign.de/javainsel5/javainsel04_006.htm

        System.out.println("			INTPUT:");
        System.out.println("Geografische Breite			Latitude		" + StringDec(latitude, 5) + " Grad");
        System.out.println("Östl. geografische Länge		Longitude		" + StringDec(longitude, 5) + " Grad");
        System.out.println("Höhe über dem Meeresspiegel 		Height above sea level	" + deltaH + " Meter");
        System.out.println("Tag.Monat.Jahr 				Day.Month.Year		" + day + "." + month + "." + year);
        System.out.println("Stunde:Minute:Sekunde 			Hour:Minute:Second	" + hour + ":" + minute + ":" + second);
        System.out.println("Zeitdifferenz zu Weltzeit UTC: 1h = Winterz., 2h = Sommerz.	" + localgmtdiff + " h");

        // Julian date
        double JD0 = calcJD(day, month, year);
        double JD = JD0 + (hour - localgmtdiff + minute / 60. + second / 3600.) / 24.;
        double TDT = JD + deltaT / 24. / 3600.;

        double latRad = latitude * DEG; // geodetic latitude of observer on WGS84
        double lonRad = longitude * DEG; // latitude of observer
        double height = deltaH * 0.001; // altiude of observer in meters above WGS84 ellipsoid (and converted to
                                        // kilometers)

        double gmst = gMST(JD);
        double lmst = gMST2LMST(gmst, lonRad);

        Coor observerCart = observer2EquCart(lonRad, latRad, height, gmst); // geocentric cartesian coordinates of
                                                                            // observer

        Coor sunCoor = sunPosition(TDT, latRad, lmst * 15. * DEG); // Calculate data for the Sun at given time
        Coor moonCoor = moonPosition(sunCoor, TDT, observerCart, lmst * 15. * DEG); // Calculate data for the Moon at
                                                                                    // given time

        System.out.println("			OUTPUT:");
        System.out.println("Julianisches Datum			JD			" + StringDec(JD, 5) + " Tage");
        System.out.println("Greenwich Sternzeit GMST		GMST			" + timeHHMMSSdec(gmst) + " h");
        System.out.println(
                "Lokale Sternzeit LMST			LMST			" + timeHHMMSSdec(gMST2LMST(gmst, lonRad)) + " h");

        // Calculate distance from the observer (on the surface of earth) to the center of the sun
        Coor sunCart = equPolar2Cart(sunCoor.ra, sunCoor.dec, sunCoor.distance);
        String SunDistanceObserver = StringDec(Math.sqrt(
                sqr(sunCart.x - observerCart.x) + sqr(sunCart.y - observerCart.y) + sqr(sunCart.z - observerCart.z)),
                3);

        // JD0: JD of 0h UTC time
        Riseset sunRise = sunRise(JD0, deltaT, lonRad, latRad, localgmtdiff + 0.0, false);

        // Calculate distance from the observer (on the surface of earth) to the center of the moon
        Coor moonCart = equPolar2Cart(moonCoor.raGeocentric, moonCoor.decGeocentric, moonCoor.distance);
        String moonDistanceObserver = StringDec(Math.sqrt(
                sqr(moonCart.x - observerCart.x) + sqr(moonCart.y - observerCart.y) + sqr(moonCart.z - observerCart.z)),
                3);
        Riseset moonRise = moonRise(JD0, deltaT, lonRad, latRad, localgmtdiff, false);

        System.out.println("			SONNE:");
        System.out.println(
                "Entfernung der Sonne (Erdmittelpunkt)	SunDistance		" + StringDec(sunCoor.distance, 3) + " km");
        System.out
                .println("Entfernung der Sonne (vom Beobachter)	SunDistanceObserver	" + (SunDistanceObserver) + " km");
        System.out.println(
                "Eklipt. Länge der Sonne			SunLon			" + StringDec(sunCoor.lon * RAD, 3) + " Grad");
        System.out.println(
                "Rektaszension der Sonne			SunRA			" + timeHHMMdec(sunCoor.ra * RAD / 15) + " Grad");
        System.out
                .println("Deklination der Sonne			SunDec			" + StringDec(sunCoor.dec * RAD, 3) + " Grad");
        System.out.println("Azimut der Sonne			SunAz			" + StringDec(sunCoor.az * RAD, 3) + " Grad");
        System.out.println("Höhe der Sonne über Horizont		SunAlt			"
                + StringDec(sunCoor.alt * RAD + refraction(sunCoor.alt), 3) + " Grad");// including refraction
        System.out.println(
                "Durchmesser der Sonne			SunDiameter		" + StringDec(sunCoor.diameter * RAD * 60., 3) + " '"); // angular
                                                                                                                        // diameter
                                                                                                                        // in
                                                                                                                        // arc
                                                                                                                        // seconds

        System.out.println("Astronomische Morgendämmerung	SunAstronomicalTwilightMorning	"
                + timeHHMMdec(sunRise.astronomicalTwilightMorning) + " h");
        System.out.println("Nautische Morgendämmerung	SunNauticalTwilightMorning	"
                + timeHHMMdec(sunRise.nauticalTwilightMorning) + " h");
        System.out.println("Bürgerliche Morgendämmerung	SunCivilTwilightMorning		"
                + timeHHMMdec(sunRise.cicilTwilightMorning) + " h");
        System.out.println("Sonnenaufgang			SunRise				" + timeHHMMdec(sunRise.rise) + " h");
        System.out.println("Sonnenkulmination		SunTransit			" + timeHHMMdec(sunRise.transit) + " h");
        System.out.println("Sonnenuntergang			SunSet				" + timeHHMMdec(sunRise.set) + " h");
        System.out.println("Bürgerliche Abenddämmerung	SunCivilTwilightEvening		"
                + timeHHMMdec(sunRise.cicilTwilightEvening) + " h");
        System.out.println("Nautische Abenddämmerung	SunNauticalTwilightEvening	"
                + timeHHMMdec(sunRise.nauticalTwilightEvening) + " h");
        System.out.println("Astronomische Abenddämmerung	SunAstronomicalTwilightEvening	"
                + timeHHMMdec(sunRise.astronomicalTwilightEvening) + " h");
        System.out.println("Tierkreiszeichen			SunSign			" + sunCoor.sign);

        System.out.println("			MOND:");
        System.out.println("Entfernung des Mondes (Erdmittelpunkt)	MoonDistance		"
                + StringDec(moonCoor.distance, 3) + " km");
        System.out.println(
                "Entfernung des Mondes (vom Beobachter)	MoonDistanceObserver	" + (moonDistanceObserver) + " km");
        System.out
                .println("Eklipt. Länge des Mondes		MoonLon			" + StringDec(moonCoor.lon * RAD, 3) + " Grad");
        System.out
                .println("Eklipt. Breite des Mondes		MoonLat			" + StringDec(moonCoor.lat * RAD, 3) + " Grad");
        System.out.println(
                "Rektaszension des Mondes		MoonRA			" + timeHHMMdec(moonCoor.ra * RAD / 15.) + " h");
        System.out.println(
                "Deklination des Mondes			MoonDec			" + StringDec(moonCoor.dec * RAD, 3) + " Grad");
        System.out.println("Azimut des Mondes			MoonAz			" + StringDec(moonCoor.az * RAD, 3) + " Grad");
        System.out.println("Höhe des Mondes über Horizont		MoonAlt			"
                + StringDec(moonCoor.alt * RAD + refraction(moonCoor.alt), 3) + " Grad"); // including refraction
        System.out.println("Durchmesser des Mondes			MoonDiameter		"
                + StringDec(moonCoor.diameter * RAD * 60., 3) + " '"); // angular diameter in arc seconds

        System.out.println("Mondaufgang				MoonRise		" + timeHHMMdec(moonRise.rise) + " h");
        System.out.println("Mondkulmination				MoonTransit		" + timeHHMMdec(moonRise.transit) + " h");
        System.out.println("Monduntergang				MoonSet			" + timeHHMMdec(moonRise.set) + " h");
        System.out.println("Mondphase				MoonPhaseNumber		" + StringDec(moonCoor.phase, 3));
        System.out.println("Mondalter				MoonAge			" + StringDec(moonCoor.moonAge * RAD, 3) + " Grad");
        System.out.println("Mondphase				MoonPhase		" + moonCoor.moonPhase);
        System.out.println("Mondzeichen				MoonSign		" + moonCoor.sign);

    }

    private static void currentTimeAndDate(String zonestr) {
        // Date and Time
        current_time = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        local_time = Calendar.getInstance(TimeZone.getTimeZone(zonestr));
        year = current_time.get(Calendar.YEAR);
        month = (current_time.get(Calendar.MONTH) + 1);
        day = current_time.get(Calendar.DAY_OF_MONTH);
        hour = current_time.get(Calendar.HOUR_OF_DAY);
        minute = current_time.get(Calendar.MINUTE);
        second = current_time.get(Calendar.SECOND);
        localgmtdiff = local_time.get(Calendar.HOUR_OF_DAY) - hour; // Timezone
    }

    private static void currentTimeAndDateTEST() {
        // Date and Time
        current_time = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        local_time = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        year = current_time.get(Calendar.YEAR);
        month = (current_time.get(Calendar.MONTH) + 1);
        day = current_time.get(Calendar.DAY_OF_MONTH);
        hour = 8;
        minute = 10;
        second = 10;
        localgmtdiff = 2; // Timezone
    }

}
