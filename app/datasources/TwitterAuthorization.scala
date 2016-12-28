package datasources

import org.slf4j.LoggerFactory
import twitter4j.{Twitter, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder
/**
  * Created by vvass on 12/27/16.
  */

private object TwitterAuthorization {
  
  // TODO need to move this to configuration, add second level of encryption and security
  private val api_key = "bgzvkOLbYmcw22uBkyV2gNVGQ"
  private val api_secret = "aFvEvKrwULWDUaPM6XDCk4z3M7rKDLkaa41HhPPXUaAyPpC2d1"
  private val access_token = "710064201293811712-HiPNCvhCCRGuO1ZnmxTSJ1V4b7sv8dK"
  private val access_token_secret = "VdHnygXdQnxY0JTIyJMMYqCgHKvF1T2CTRoWNYjkv3rta"
  
  private def client(): Twitter = {
    lazy val logger = LoggerFactory.getLogger(classOf[TwitterAuthorization])
    logger.debug("Object initilized and auth keys processed") // TODO add this to configuration
  
    val configBuilder = new ConfigurationBuilder()
  
    configBuilder.setDebugEnabled(true)
      .setOAuthConsumerKey(api_key)
      .setOAuthConsumerSecret(api_secret)
      .setOAuthAccessToken(access_token)
      .setOAuthAccessTokenSecret(access_token_secret)
  
    val tf = new TwitterFactory(configBuilder.build())
  
    tf.getInstance()
  }
}

class TwitterAuthorization() {
  import TwitterAuthorization._
  
  lazy val logger = LoggerFactory.getLogger(classOf[TwitterAuthorization])
  
  logger.debug("Class called") // TODO add this to configuration
  
  def getClientAPI: Twitter = client()
}


