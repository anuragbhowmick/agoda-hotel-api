package controllers

import javax.inject.Singleton
import api.APICallRateLimiter
import config.ConfigLoader
import scala.collection.mutable

/**
  * Implementation of APICallRateLimiter
  * We maintain a concurrent map of apiKey to requestQueue
  * The request queue store the timestamp of the requests.
  * The length of the queue will be <= the rate limit for the apiKey.
  *
  * On getting a request :
  * 1. Check if the apiKey is suspended
  * 2. If length of queue < rateLimit. Accept the request
  * 3. if currentTime-head(queue) > rateLimitTimeThreshold
  *   a). Accept request
  *   b). Deque and enque the last request timestamp
  * 4. Suspend the key otherwise
  *
  */
@Singleton
class InMemoryAPICallRateLimiter(val configLoader: ConfigLoader) extends APICallRateLimiter {

  private var suspendedApiKeys          = scala.collection.concurrent.TrieMap[String, Long]()
  private var apiKeyToRequestQueueMap   = scala.collection.concurrent.TrieMap[String, collection.mutable.Queue[Long]]()

  override def hasRateLimitExceeded(apiKey: String) : Boolean = {
    val currentTime = System.currentTimeMillis()
    if(isApiKeySuspended(apiKey, currentTime))
      return true
    val rateLimit = getRateLimit(apiKey)

    if(apiKeyToRequestQueueMap.contains(apiKey)) {
      var queue = apiKeyToRequestQueueMap.get(apiKey).get
      if(queue.length < rateLimit) {
        queue.synchronized {
          queue.enqueue(currentTime)
        }
      } else if((currentTime - queue.front) > configLoader.getRateLimitTimeThresholdMs()) {
        queue.synchronized {
          queue.dequeue()
          queue.enqueue(currentTime)
        }
      } else {
        suspendAPIKey(apiKey, currentTime)
        return true
      }
    } else {
      apiKeyToRequestQueueMap.putIfAbsent(apiKey, mutable.Queue(currentTime))
    }
    return false
  }

  /**
    * get the rate limit for the given key. Will return the default rate limit in case the value is not present
    * @param apiKey
    * @return
    */
  private def getRateLimit(apiKey: String) : Long = {
    if(configLoader.getRateLimitMap().contains(apiKey)) {
      configLoader.getRateLimitMap().get(apiKey).get.toLong
    } else {
      configLoader.getDefaultRateLimitRequestCount()
    }
  }

  /**
    * Check if the api key is suspended
    * @param apiKey
    * @param currentTime
    * @return
    */
  private def isApiKeySuspended(apiKey : String, currentTime : Long): Boolean = {
    if(suspendedApiKeys.contains(apiKey)) {
      return ((currentTime - suspendedApiKeys.get(apiKey).get) < configLoader.getDefaultApiKeySuspensionMs())
    }
    return false
  }


  /**
    * suspend the key for the specified time
    * @param apiKey
    * @param time
    */
  private def suspendAPIKey(apiKey : String, time : Long): Unit = {
    suspendedApiKeys(apiKey) = time
  }
}
