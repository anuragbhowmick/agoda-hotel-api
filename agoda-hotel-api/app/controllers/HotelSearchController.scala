package controllers

import play.api.mvc._
import javax.inject.Inject
import config._
import api._
import javax.inject.Singleton
import model.SortingType

/**
  * Created by anurag on 2/28/17.
  */
@Singleton
class HotelSearchController @Inject()(val hotelDataAccessor : InMemoryHotelDataAccessor,
                                      val configLoader : ConfigLoader) extends Controller {

  val QUERY_SORT : String    = "sort"
  val API_KEY_NAME : String  = "x-api-key"

  val apiCallRateLimiter: APICallRateLimiter = new InMemoryAPICallRateLimiter(configLoader)

  def search(city : String) = Action {
    request =>
      var apiKey: String = null
      var orderBy : String = null

      if(request.headers.toMap.contains(API_KEY_NAME))
        apiKey = request.headers.get(API_KEY_NAME).get
      if(request.queryString.contains(QUERY_SORT)) {
        orderBy = request.queryString.get(QUERY_SORT).get.mkString
      }
      handleRequest(apiKey, city, orderBy)
  }

  def handleRequest(authKey : String, city : String, orderBy : String = null): Result = {
    if(authKey == null)
      return Status(400) ("Bad Request. Missing request header " + API_KEY_NAME)
    if(!configLoader.apiKeys.contains(authKey))
      return Status(400) ("Bad Request. Invalid " + API_KEY_NAME + " : " + authKey)
    if (apiCallRateLimiter.hasRateLimitExceeded(authKey))
      return Status(429) ("Too many requests for API key. Try after some time " + authKey)

    var sortType:SortingType.SortingType = null
    if(orderBy != null && SortingType.isValidType(orderBy.toUpperCase)) {
      sortType = SortingType.withName(orderBy.toUpperCase())
    }
    val hotelList = hotelDataAccessor.getHotelsForCity(city, sortType)
    var result : String = ""
    if(!hotelList.isEmpty)
      result = hotelList mkString(",")
    result = "[" + result + "]"
    return Ok(result)
  }
}
