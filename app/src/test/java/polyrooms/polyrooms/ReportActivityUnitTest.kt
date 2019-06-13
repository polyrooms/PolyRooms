package polyrooms.polyrooms

import org.junit.Test
import org.junit.Assert.*

class ReportActivityUnitTest {

    @Test
    fun testNullStringBuilding() {
        val nullstring : String? = null
        assertFalse(checkBuildingNum(nullstring))
    }

    @Test
    fun testNullStringRoom() {
        val nullstring : String? = null
        assertFalse(checkRoomNum(nullstring))
    }

    @Test
    fun testNullStringDescription() {
        val nullstring : String? = null
        assertFalse(checkDescription(nullstring))
    }

    @Test
    fun testEmptyStringBuilding() {
        val emptystring = ""
        assertFalse(checkBuildingNum(emptystring))
    }

    @Test
    fun testEmptyStringRoom() {
        val emptystring = ""
        assertFalse(checkRoomNum(emptystring))
    }

    @Test
    fun testEmptyStringDescription() {
        val emptystring = ""
        assertFalse(checkDescription(emptystring))
    }

    @Test
    fun testTrueBuilding() {
        val nonemptystring = "101A"
        assertTrue(checkBuildingNum(nonemptystring))
    }

    @Test
    fun testTrueRoom() {
        val nonemptystring = "101A"
        assertTrue(checkRoomNum(nonemptystring))
    }

    @Test
    fun testTrueDescription() {
        val nonemptystring = "This is an example description."
        assertTrue(checkDescription(nonemptystring))
    }
}
