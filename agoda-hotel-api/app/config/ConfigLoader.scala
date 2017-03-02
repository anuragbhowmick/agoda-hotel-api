package config

import javax.inject.Singleton
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}

import scala.util.parsing.json._

/**
  * For loading the configurations for the api keys. COnfiguration loaded are :
  * 1. The valid api keys
  * 2. The key to rate limit map
  * 3. Default rate limit in case key to rate limit is not valid
  * 4. Default time for which an api key will be suspended
  * 5. The time interval agaist which the rate limit will be validated
  *
  */
@Singleton
class ConfigLoader {

  val config = ConfigFactory.load("apikeys.conf")
  val configJSON : String = config.root().render(ConfigRenderOptions.concise())
  val parsed = JSON.parseFull(configJSON)
  val globalMap = parsed.get.asInstanceOf[Map[String, Any]]

  val apiKeys = globalMap.get("allowedApiKeys").get.asInstanceOf[List[String]]
  val rateLimits = globalMap.get("rateLimits").get.asInstanceOf[Map[String, String]]
  val defaultRateLimit = globalMap.get("defaultRateLimit").get.asInstanceOf[String]
  val defaultAPIKeySuspensionMS = globalMap.get("defaultAPIKeySuspensionMs").get.asInstanceOf[String]
  val rateLimitTimeThresholdMs = globalMap.get("rateLimitTimeThresholdMs").get.asInstanceOf[String]

  def getApiKeys() : List[String] = apiKeys
  def getRateLimitMap() : Map[String, String] = rateLimits
  def getDefaultRateLimitRequestCount() : Long = defaultRateLimit.toLong
  def getDefaultApiKeySuspensionMs() : Long = defaultAPIKeySuspensionMS.toLong
  def getRateLimitTimeThresholdMs() : Long = rateLimitTimeThresholdMs.toLong
}