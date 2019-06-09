package polyrooms.polyrooms
/*
 *
 *  This class implements the singleton design pattern.
 *  Only one BuildingsInfo object is ever needed.
 *  In Kotlin, a singleton is declared with the `object` keyword.
 *  Reference: https://kotlinlang.org/docs/reference/object-declarations.html#object-declarations
 *
 */

object BuildingsInfo {

    private val buildingNums = listOf<Int>(2, 3, 5, 6, 8, 10, 11, 13, 14, 20, 21, 22, 24, 26, 33, 34, 35, 38,
            41, 42, 43, 44, 45, 52, 53, 65, 180, 186, 192)

    private val coordinateMap: HashMap<Int, Pair<Double, Double>> =
            hashMapOf(2 to Pair(35.3002, -120.664366),
                    3 to Pair(35.299963, -120.664958),
                    5 to Pair(35.300774, -120.664253),
                    6 to Pair(35.2994897, -120.657222929),
                    8 to Pair(35.30313581986, -120.661577487),
                    10 to Pair(35.30191949, -120.6614847),
                    11 to Pair(35.302876, -120.66294743456997),
                    14 to Pair(35.30016682668, -120.6622496),
                    20 to Pair(35.3002872728, -120.661805676),
                    21 to Pair(35.30028628549, -120.66291230814),
                    22 to Pair(35.302180773738, -120.6609149068),
                    24 to Pair(35.303655767699, -120.66300528),
                    26 to Pair(35.29928447, -120.66174983859807),
                    33 to Pair(35.30215046, -120.659217541639),
                    34 to Pair(35.3010928255, -120.6633926245),
                    35 to Pair(35.301776634969, -120.6637375024826),
                    38 to Pair(35.30169532496868, -120.6622671768),
                    41 to Pair(35.30259454455, -120.665884672335),
                    42 to Pair(35.29875536065, -120.6587810452928),
                    43 to Pair(35.2981670597, -120.6601552738),
                    44 to Pair(35.299756787, -120.65775878460317),
                    45 to Pair(35.299309409338, -120.657957415),
                    52 to Pair(35.30038387486661, -120.6601558485097),
                    53 to Pair(35.301924405, -120.65986173796),
                    65 to Pair(35.3001769875377, -120.65870985667405),
                    180 to Pair(35.3012377, -120.6607268),
                    186 to Pair(35.2992556819, -120.662830613127),
                    192 to Pair(35.3029156, -120.6647912509))


    fun getBuildingNums() : List<Int> {
        return buildingNums
    }

    fun getCoordinates() : HashMap<Int, Pair<Double, Double>> {
        return coordinateMap
    }

}