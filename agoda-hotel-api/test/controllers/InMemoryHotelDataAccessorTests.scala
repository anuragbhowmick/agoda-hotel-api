package controllers

import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import model.{Hotel, SortingType}

import scala.collection.mutable
/**
  * Created by anurag on 3/2/17.
  */
class InMemoryHotelDataAccessorTests extends AssertionsForJUnit{

  var inMemoryHotelDataAccessor : InMemoryHotelDataAccessor = _

  @Before
  def initialize(): Unit=  {
    inMemoryHotelDataAccessor = new InMemoryHotelDataAccessor
  }

  @Test
  def getHotelsForCityTest(): Unit = {
    var list = inMemoryHotelDataAccessor.getHotelsForCity("Bangkok")
    assertEquals(list.size, 7)

    list = inMemoryHotelDataAccessor.getHotelsForCity("Mumbai")
    assertEquals(list.size,0)
  }

  @Test
  def getHotelsForCitySortedTest(): Unit = {
    var list1 = inMemoryHotelDataAccessor.getHotelsForCity("Bangkok", SortingType.PRICEDESC)
    assertEquals(list1.size, 7)
    var priceList1 = mutable.MutableList[Int]()
    var descList = mutable.MutableList[Int]()
    for(hotel <- list1) {
      priceList1 += hotel.price
      descList += hotel.price
    }
    descList = descList.sortWith(_ > _)
    assertTrue(descList.equals(priceList1))

    var list2 = inMemoryHotelDataAccessor.getHotelsForCity("Amsterdam", SortingType.PRICEASC)
    assertEquals(list2.size, 6)
    var priceList2 = mutable.MutableList[Int]()
    var ascList = mutable.MutableList[Int]()
    for(hotel <- list2) {
      priceList2 += hotel.price
      ascList += hotel.price
    }
    ascList = ascList.sortWith(_ < _)
    assertTrue(ascList.equals(priceList2))

  }
}
