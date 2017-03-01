package model

/**
  * Created by anurag on 2/28/17.
  */
class Hotel(val c: String, val id: String, val rt: String, val pr: Int) {
  var city: String = c
  var hotelId: String = id
  var roomType: String = rt
  var price: Int = pr

  override def toString: String = {
    "[" + hotelId + "," + city + "," + roomType + "," + price + "]"
  }
}
