package com.example

import com.example.data.model.NutritionCategory
import com.example.data.model.RiskLevel
import com.example.data.nutrition.NutritionCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testBmiCalculation() {
    // BB: 58kg, TB: 168cm -> IMT = 58 / (1.68 * 1.68) = 20.55 -> rounded 20.5
    val bmi = NutritionCalculator.calculateBmi(58.0, 168.0)
    assertEquals(20.5, bmi, 0.1)
  }

  @Test
  fun testBmiClassificationPermenkes() {
    // Remaja laki-laki usia 16 tahun dengan IMT 20.5 -> Normal, Low Risk
    val resultNormal = NutritionCalculator.evaluateBmiAge(20.5, 16, "L")
    assertEquals(NutritionCategory.NORMAL, resultNormal.category)
    assertEquals(RiskLevel.LOW, resultNormal.riskLevel)

    // Remaja perempuan usia 16 tahun dengan IMT 15.0 -> Underweight
    val resultUnder = NutritionCalculator.evaluateBmiAge(15.0, 16, "P")
    assertEquals(NutritionCategory.UNDERWEIGHT, resultUnder.category)
    assertEquals(RiskLevel.MEDIUM, resultUnder.riskLevel)

    // Remaja perempuan usia 15 tahun dengan IMT 13.5 -> Very Underweight
    val resultVeryUnder = NutritionCalculator.evaluateBmiAge(13.5, 15, "P")
    assertEquals(NutritionCategory.VERY_UNDERWEIGHT, resultVeryUnder.category)
    assertEquals(RiskLevel.HIGH, resultVeryUnder.riskLevel)

    // Remaja laki-laki usia 17 tahun dengan IMT 32.0 -> Obese
    val resultObese = NutritionCalculator.evaluateBmiAge(32.0, 17, "L")
    assertEquals(NutritionCategory.OBESE, resultObese.category)
    assertEquals(RiskLevel.HIGH, resultObese.riskLevel)
  }

  @Test
  fun testBmrAndTdeeFormulas() {
    // Laki-laki BB: 60kg, TB: 170cm, Usia: 16
    // BMR = 88.362 + (13.397 * 60) + (4.799 * 170) - (5.677 * 16)
    // = 88.362 + 803.82 + 815.83 - 90.832 = 1617.18
    val bmrMale = NutritionCalculator.calculateBmr(60.0, 170.0, 16, isMale = true)
    assertTrue(bmrMale in 1600.0..1630.0)

    val tdee = NutritionCalculator.calculateTdee(bmrMale, 1.55)
    assertTrue(tdee > bmrMale)
    assertEquals(bmrMale * 1.55, tdee, 0.1)
  }
}
