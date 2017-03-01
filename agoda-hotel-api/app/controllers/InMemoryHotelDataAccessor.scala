package controllers

import java.io.FileNotFoundException
import java.io._
import javax.inject.Singleton

import api.HotelDataAccessor
import model.{Hotel, SortingType}
import model.SortingType.SortingType
import play.api.libs.json.Json

/**
  * Created by anurag on 3/1/17.
  */
@Singleton
class InMemoryHotelDataAccessor extends HotelDataAccessor {

  var cityToHotel = scala.collection.mutable.Map[String, List[Hotel]]()

  val init = {
    try {
      val bufferedSource = scala.io.Source.fromFile("hoteldb.csv")
      var count = 0
      for (line <- bufferedSource.getLines) {
        val cols = line.split(",").map(_.trim)
        val hotel = new Hotel(cols(0), cols(1), cols(2), cols(3).toInt)
        cityToHotel.get(hotel.city) match {
          case Some(xs: List[Hotel]) => {
            cityToHotel.update(hotel.city, xs :+ hotel)
          }
          case None => {
            cityToHotel(hotel.city) = List(hotel)
          }
        }
      }
      bufferedSource.close
    } catch {
      case ex: FileNotFoundException => println("Couldn't find that file.")
      case ex: IndexOutOfBoundsException => println("Had an IOException trying to read that file")
    }
  }

  override def getHotelsForCity(city: String, sortType : SortingType = null): List[Hotel] = {
    cityToHotel.get(city) match {
      case Some(hotelList:List[Hotel]) =>
        sortType match {
          case SortingType.PRICEDESC => hotelList.sortWith(_.price > _.price)
          case SortingType.PRICEASC => hotelList.sortWith(_.price < _.price)
          case default => hotelList
        }
      case None => List[Hotel]()
    }
  }
}
