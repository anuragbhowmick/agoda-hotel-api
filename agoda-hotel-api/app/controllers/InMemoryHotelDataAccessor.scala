package controllers

import java.io.FileNotFoundException
import javax.inject.Singleton

import api.HotelDataAccessor
import model.{Hotel, SortingType}
import model.SortingType.SortingType

/**
  * Implementation of HotelDataAccessor
  * Stores the hotel details inMemory
  */
@Singleton
class InMemoryHotelDataAccessor extends HotelDataAccessor {

  private val HOTEL_DB_FILE = "hoteldb.csv"
  private var cityToHotel = scala.collection.mutable.Map[String, List[Hotel]]()

  /**
    * Reads the csv file and populates a map of city to hotel List
    */
  private val init = {
    try {
      val bufferedSource = scala.io.Source.fromFile(HOTEL_DB_FILE)
      var count = 0
      for (line <- bufferedSource.getLines) {
        try {
          val cols = line.split(",").map(_.trim)
          val hotel = new Hotel(cols(0), cols(1), cols(2), cols(3).toInt)
          cityToHotel.get(hotel.city) match {
            case Some(hotelList: List[Hotel]) => cityToHotel.update(hotel.city, hotelList :+ hotel)
            case None => cityToHotel(hotel.city) = List(hotel)
          }
        } catch {
          case ex:Exception => println("Exception in reading line " + ex)
        }
      }
      bufferedSource.close
    } catch {
      case ex: FileNotFoundException => println("Couldn't find file" + HOTEL_DB_FILE)
      case ex: Exception => println("Exception while reading from file " + HOTEL_DB_FILE + ex)
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
