package api

import model.SortingType.SortingType
import model.Hotel

/**
  * Created by anurag on 3/1/17.
  */
trait HotelDataAccessor {
  /**
    * Query Hotel details based on city name
    *
    * @param city           The city for which we want the list of hotels
    * @param sortType       Optional sorting attribute. PriceAsc/PriceDesc
    * @return               A List of found hotels(can be sorted based on the sort paramaeter) or null
    */
  def getHotelsForCity(city : String, sortType : SortingType = null) : List[Hotel]
}
