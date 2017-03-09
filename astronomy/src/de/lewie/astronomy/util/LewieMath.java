package de.lewie.astronomy.util;
/**
*
* Copyright (C) 2010-2017, Lewi Cleantech GmbH <info@lewi-cleantech.de>
*
* @author Helmut Lehmeyer
* @date 09.03.2012
* @version 0.1
*/

import java.text.DecimalFormat;

public class LewieMath {

    public static double pi = Math.PI;
    public final static double DEG = pi / 180.0;
    public static double RAD = 180. / pi;

    // return integer value, closer to 0
    public static int Int(double x) {
        if (x < 0) {
            return (int) (Math.ceil(x));
        } else {
            return (int) (Math.floor(x));
        }
    }

    public static double sqr(double x) {
        return x * x;
    }

    public static double frac(double x) {
        return (x - Math.floor(x));
    }

    public static double mod(double a, double b) {
        return (a - Math.floor(a / b) * b);
    }

    public static double mod2Pi(double x) {
        return (mod(x, 2. * pi));
    } // Modulo PI

    /**
     *
     * @param x
     * @param s
     * @return
     */
    public static String StringDec(double x, int s) {
        String form = ".00000000000000000000";
        if (s > 0) {
            s++;// add point
        }
        return (new DecimalFormat(",##0" + form.substring(0, s)).format(x));
    }

    /**
     * using for "double" trouble
     *
     * @param x
     * @param p
     * @return
     */
    public static double round(double x, int p) {
        return (Math.round(Math.pow(10, p) * x) / Math.pow(10, p));
    }

    /**
     * using for "double" trouble
     * Floating-Point Arithmetic
     * http://docs.oracle.com/cd/E19957-01/806-3568/ncg_goldberg.html
     * http://www.heise.de/ct/hotline/Java-rechnet-falsch-310780.html
     * http://stackoverflow.com/questions/356807/java-double-comparison-epsilon
     */
    private static double EPSILON = 0.00001;

    /**
     * @return the epsilon
     */
    public static double getEpsilon() {
        return EPSILON;
    }

    /**
     * @param epsilon the epsilon to set
     */
    public static void setEpsilon(double epsilon) {
        EPSILON = epsilon;
    }

    /**
     * Returns true if two doubles are considered equal. Tests if the absolute
     * difference between two doubles has a difference less then .00001. This
     * should be fine when comparing prices, because prices have a precision of
     * .001.
     *
     * @param a double to compare.
     * @param b double to compare.
     * @return true true if two doubles are considered equal.
     */
    public static boolean equals(double a, double b) {
        return a == b ? true : Math.abs(a - b) < EPSILON;
    }

    /**
     * Returns true if two doubles are considered equal. Tests if the absolute
     * difference between the two doubles has a difference less then a given
     * double (epsilon). Determining the given epsilon is highly dependant on the
     * precision of the doubles that are being compared.
     *
     * @param a double to compare.
     * @param b double to compare
     * @param epsilon double which is compared to the absolute difference of two
     *            doubles to determine if they are equal.
     * @return true if a is considered equal to b.
     */
    public static boolean equals(double a, double b, double epsilon) {
        return a == b ? true : Math.abs(a - b) < epsilon;
    }

    /**
     * Returns true if the first double is considered greater than the second
     * double. Test if the difference of first minus second is greater then
     * .00001. This should be fine when comparing prices, because prices have a
     * precision of .001.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered greater than the second
     *         double
     */
    public static boolean greaterThan(double a, double b) {
        return greaterThan(a, b, EPSILON);
    }

    /**
     * Returns true if the first double is considered greater than the second
     * double. Test if the difference of first minus second is greater then
     * a given double (epsilon). Determining the given epsilon is highly
     * dependant on the precision of the doubles that are being compared.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered greater than the second
     *         double
     */
    public static boolean greaterThan(double a, double b, double epsilon) {
        return a - b > epsilon;
    }

    /**
     * Returns true if the first double is considered less than the second
     * double. Test if the difference of second minus first is greater then
     * .00001. This should be fine when comparing prices, because prices have a
     * precision of .001.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered less than the second
     *         double
     */
    public static boolean lessThan(double a, double b) {
        return lessThan(a, b, EPSILON);
    }

    /**
     * Returns true if the first double is considered less than the second
     * double. Test if the difference of second minus first is greater then
     * a given double (epsilon). Determining the given epsilon is highly
     * dependant on the precision of the doubles that are being compared.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered less than the second
     *         double
     */
    public static boolean lessThan(double a, double b, double epsilon) {
        return b - a > epsilon;
    }

}
