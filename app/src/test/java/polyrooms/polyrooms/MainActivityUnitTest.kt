package polyrooms.polyrooms

import org.junit.Test
import org.junit.Assert.*

class MainActivityUnitTest {
    @Test
    fun testGetAPam() {
        val hour: Int = 12
        val ampm: String = getAP(hour)
        assertEquals("am", ampm)
    }

    @Test
    fun testGetAPpm() {
        val hour: Int = 13
        val ampm: String = getAP(hour)
        assertEquals("pm", ampm)
    }

    @Test
    fun testConvertHourOne() {
        val hour: Int = 23
        assertEquals(11, convertH(hour))
    }

    @Test
    fun testConvertHourTwo() {
        val hour: Int = 0
        assertEquals(12, convertH(hour))
    }

    @Test
    fun testConvertHourThree() {
        val hour: Int = 11
        assertEquals(11, convertH(hour))
    }

    @Test
    fun testConvertMinuteOne() {
        val min: Int = 5
        assertEquals("05", convertM(min))
    }
    @Test
    fun testConvertMinuteTwo() {
        val min: Int = 15
        assertEquals("15", convertM(min))

    }
}
