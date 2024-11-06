package it.smartcommunitylab.pgazienda.dto;

import it.smartcommunitylab.pgazienda.PGAziendaApp;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = PGAziendaApp.class)
public class TransportStatDTOTest {

    private TransportStatDTO transportStatDTO;

    /**
     * Creates a new instance of {@link TransportStatDTO} before each test.
     */
    @BeforeEach
    public void setup() {
        transportStatDTO = new TransportStatDTO();
    }

    /**
     * Verifies that the {@link TransportStatDTO#setPeriod(String)} and
     * {@link TransportStatDTO#getPeriod()} methods work as expected.
     */
    @Test
    public void testSetAndGetPeriod() {
        String period = "2024-10";
        transportStatDTO.setPeriod(period);
        assertEquals(period, transportStatDTO.getPeriod(), "The period should be set and retrieved correctly.");
    }

    /**
     * Verifies that the {@link TransportStatDTO#setMean(String)} and
     * {@link TransportStatDTO#getMean()} methods work as expected.
     */
    @Test
    public void testSetAndGetMean() {
        String mean = "Bus";
        transportStatDTO.setMean(mean);
        assertEquals(mean, transportStatDTO.getMean(), "The mean should be set and retrieved correctly.");
    }

    /**
     * Verifies that the {@link TransportStatDTO#setValue(double)} and
     * {@link TransportStatDTO#getValue()} methods work as expected.
     */
    @Test
    public void testSetAndGetValue() {
        double value = 123.45;
        transportStatDTO.setValue(value);
        assertEquals(value, transportStatDTO.getValue(), "The value should be set and retrieved correctly.");
    }

    /**
     * Verifies that the default value of the {@link TransportStatDTO#getValue()}
     * method is 0.0.
     */
    @Test
    public void testDefaultValue() {
        assertEquals(0.0, transportStatDTO.getValue(), "The default value should be 0.0.");
    }
}
