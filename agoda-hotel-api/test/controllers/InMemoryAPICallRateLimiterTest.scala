package controllers

import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar


class InMemoryAPICallRateLimiterTest extends AssertionsForJUnit with MockitoSugar{

  var inMemoryAPICallRateLimiter : InMemoryAPICallRateLimiter = _
  val configLoader = mock[config.ConfigLoader]

  @Before
  def initialize(): Unit=  {
    inMemoryAPICallRateLimiter = new InMemoryAPICallRateLimiter(configLoader)
  }

  @Test
  def hasRateLimitExceededTest(): Unit = {
    when(configLoader.getRateLimitMap()).thenReturn(Map[String,String]())
    when(configLoader.getApiKeys()).thenReturn(List[String]())

    when(configLoader.getDefaultRateLimitRequestCount()).thenReturn(3)
    when(configLoader.getDefaultApiKeySuspensionMs()).thenReturn(7000)
    when(configLoader.getRateLimitTimeThresholdMs()).thenReturn(5000)

    var result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    Thread.sleep(2000)
    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertTrue(result)

    Thread.sleep(8000)
    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    Thread.sleep(6000)
    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    Thread.sleep(8000)
    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    Thread.sleep(2000)
    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    Thread.sleep(2000)
    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)

    Thread.sleep(1000)
    result = inMemoryAPICallRateLimiter.hasRateLimitExceeded("testKey")
    assertFalse(result)
  }
}
