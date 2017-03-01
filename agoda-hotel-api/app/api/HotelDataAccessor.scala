package api

import model.SortingType.SortingType
import model.Hotel

/**
  * Created by anurag on 3/1/17.
  */
trait HotelDataAccessor {
  /**
    * Hotel searches based on city name
    *
    * @param city           The city for which we want the list of hotels
    * @param sortType       Optional sorting attribute. PriceAsc/PriceDesc
    * @return               A List of found hotels or null
    */
  def getHotelsForCity(city : String, sortType : SortingType = null) : List[Hotel]
}
