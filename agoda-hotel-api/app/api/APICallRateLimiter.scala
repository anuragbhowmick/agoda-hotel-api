package api

/**
  * Created by anurag on 3/1/17.
  */
trait APICallRateLimiter {
  def hasRateLimitExceeded(apiKey : String) : Boolean
}
