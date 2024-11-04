package it.smartcommunitylab.pgazienda.domain;

import static org.junit.jupiter.api.Assertions.*;

import it.smartcommunitylab.pgazienda.PGAziendaApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest(classes = PGAziendaApp.class)
public class CircleTest {
    private Circle circle;

    /**
     * Initializes the Circle object with a center at the specified coordinates
     * and a radius of 1000 meters. This method is called before each test to
     * ensure a fresh instance of Circle is used.
     */
    @BeforeEach
    public void setup() {
        double[] center = {45.4642, 9.1900};
        double radius = 1000;
        circle = new Circle(center, radius);

        circle.setCenter(center);
        System.out.println(Arrays.toString(circle.getCenter()));
        circle.setRadius(radius);
        System.out.println(circle.getRadius());
    }

    /**
     * Verifies that a point is inside the circle.
     *
     * The test point is located at the coordinates (45.4654, 9.1910).
     *
     * @see Circle#inside(double, double)
     */
    @Test
    public void testPointInsideCircle() {
        double lat = 45.4654;
        double lon = 9.1910;
        assertTrue(circle.inside(lat, lon), "The point should be inside the circle");
    }

    /**
     * Verifies that a point is outside the circle.
     *
     * The test point is located at the coordinates (45.5000, 9.3000).
     *
     * @see Circle#inside(double, double)
     */
    @Test
    public void testPointOutsideCircle() {
        double lat = 45.5000;
        double lon = 9.3000;
        assertFalse(circle.inside(lat, lon), "The point should be outside the circle");
    }

    /**
     * Verifies that a point is on the boundary of the circle.
     *
     * The test point is located at the coordinates (45.4720, 9.1850).
     *
     * @see Circle#inside(double, double)
     */
    @Test
    public void testPointOnBoundary() {
        double lat = 45.4720;
        double lon = 9.1850;
        assertTrue(circle.inside(lat, lon), "The point should be on the boundary of the circle");
    }
}
