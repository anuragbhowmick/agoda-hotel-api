package config

import javax.inject.Singleton
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}

import scala.util.parsing.json._

/**
  * Created by anurag on 3/1/17.
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
  val defaultAPIKeySuspensionMS = globalMap.get("defaultAPIKeySuspensionMS").get.asInstanceOf[String]

  def getApiKeys() : List[String] = apiKeys
  def getRateLimitMap() : Map[String, String] = rateLimits
  def getDefaultRateLimit() : Long = defaultRateLimit.toLong
  def getDefaultApiKeySuspensionMs() : Long = defaultAPIKeySuspensionMS.toLong
}