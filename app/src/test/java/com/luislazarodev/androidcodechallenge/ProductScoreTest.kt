package com.luislazarodev.androidcodechallenge

import com.luislazarodev.androidcodechallenge.data.model.Product
import com.luislazarodev.androidcodechallenge.utils.toDoubleOrZero
import com.luislazarodev.androidcodechallenge.utils.toIntOrZero
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductScoreTest {

    @Test
    fun testScoreCalculation_normalValues() {
        val expected = (4.0 * kotlin.math.ln(10.0)) / 10.0
        val actual = Product.calculateScore(4.0, 9, 10.0)
        assertEquals(expected, actual, 0.0001)
    }

    @Test
    fun testScoreCalculation_nullValues() {
        val score = Product.calculateScore(null, null, null)
        assertEquals(0.0, score, 0.0001)
    }

    @Test
    fun testScoreCalculation_negativeValues() {
        val score = Product.calculateScore(-5.0, -10, -2.5)
        assertEquals(0.0, score, 0.0001)
    }

    @Test
    fun testScoreCalculation_zeroPrice() {
        val expected = 5.0 * kotlin.math.ln(10.0)
        val actual = Product.calculateScore(5.0, 9, 0.0)
        assertEquals(expected, actual, 0.0001)
    }

    @Test
    fun testNormalization_stringsAndValues() {
        assertEquals(10.5, "10.5".toDoubleOrZero(), 0.0001)
        assertEquals(0.0, "".toDoubleOrZero(), 0.0001)
        assertEquals(0.0, "  ".toDoubleOrZero(), 0.0001)
        assertEquals(0.0, "invalid".toDoubleOrZero(), 0.0001)
        assertEquals(0.0, null.toDoubleOrZero(), 0.0001)

        assertEquals(5, "5".toIntOrZero())
        assertEquals(0, "".toIntOrZero())
        assertEquals(0, "invalid".toIntOrZero())
        assertEquals(0, null.toIntOrZero())
    }

    @Test
    fun testProductSortingByScore() {
        val p1 = Product(1, "Low Score", "", 10.0, 1.0, 1, "In Stock", "", emptyList(), 0.1)
        val p2 = Product(2, "High Score", "", 10.0, 5.0, 9, "In Stock", "", emptyList(), 1.15)
        val p3 = Product(3, "Medium Score", "", 10.0, 3.0, 4, "In Stock", "", emptyList(), 0.48)

        val unsortedList = listOf(p1, p3, p2)
        val sortedList = unsortedList.sortedByDescending { it.score }

        assertEquals(p2.id, sortedList[0].id)
        assertEquals(p3.id, sortedList[1].id)
        assertEquals(p1.id, sortedList[2].id)
    }
}
