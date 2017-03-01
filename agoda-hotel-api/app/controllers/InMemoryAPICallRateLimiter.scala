package controllers

import javax.inject.Singleton
import api.APICallRateLimiter
import config.ConfigLoader
import scala.collection.mutable

/**
  * Created by anurag on 3/1/17.
  */
@Singleton
class InMemoryAPICallRateLimiter(val configLoader: ConfigLoader) extends APICallRateLimiter {

  println("InMemoryAPICallRateLimiter called")
  val RATE_LIMIT_INTERVAL_MS  = 10 * 1000

  var suspendedApiKeys        = scala.collection.concurrent.TrieMap[String, Long]()
  var apiKeyToRequestQueueMap     = scala.collection.concurrent.TrieMap[String, collection.mutable.Queue[Long]]()

  override def hasRateLimitExceeded(apiKey: String) : Boolean = {
    if(isApiKeySuspended(apiKey))
      return true
    val rateLimit = getRateLimit(apiKey)
    val currentTime = System.currentTimeMillis()

    if(apiKeyToRequestQueueMap.contains(apiKey)) {
      var queue = apiKeyToRequestQueueMap.get(apiKey).get
      println("request queue length is " + queue.length)
      if(queue.length < rateLimit) {
        queue.enqueue(currentTime)
      } else if((currentTime - queue.front) > RATE_LIMIT_INTERVAL_MS) {
        queue.dequeue()
        queue.enqueue(currentTime)
      } else {
        suspendAPIKey(apiKey)
        return true
      }
    } else {
      apiKeyToRequestQueueMap.putIfAbsent(apiKey, mutable.Queue(currentTime))
      println("putting in apiRequestQueuesMap")
    }
    return false
  }

  def getRateLimit(apiKey: String) : Long = {
    if(configLoader.getRateLimitMap().contains(apiKey)) {
      configLoader.getRateLimitMap().get(apiKey).get.toLong
    } else {
      configLoader.getDefaultRateLimit()
    }
  }

  def isApiKeySuspended(apiKey : String): Boolean = {
    if(suspendedApiKeys.contains(apiKey)) {
      val currentTime = System.currentTimeMillis()
      return (currentTime - suspendedApiKeys.get(apiKey).get <= configLoader.getDefaultApiKeySuspensionMs())
    }
    return false
  }

  def suspendAPIKey(apiKey : String): Unit = {
      println("suspendAPIKey")
    suspendedApiKeys(apiKey) = System.currentTimeMillis()
  }
}
