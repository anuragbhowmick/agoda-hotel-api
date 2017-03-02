package api

/**
  * Created by anurag on 3/1/17.
  */
trait APICallRateLimiter {

  /**
    * Check if the rate limit has been exceeded for the api key
    *
    * @param apiKey         The api key gaianst the limit has to be checked
    * @return               true/false
    */
  def hasRateLimitExceeded(apiKey : String) : Boolean
}
