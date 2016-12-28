package models

/**
  * Created by vvass on 12/28/16.
  */


import datasources.TwitterAuthorization
import org.slf4j.LoggerFactory
import twitter4j.{Status, StatusUpdate}


sealed trait TweetUtilityTrait {
  def toOption: Option[Any] // TODO we might need to modify this or get rid of it
  def getStatus: String
  def getOurId: Long
  def statusUpdate: StatusUpdate
}

object TweetResponseUtility {
  
  /** This Authorizes the client and init a twitter object so that we can call
      the twitter4j APIs
    */
  
  private def api = new TwitterAuthorization
  
}

class TweetResponseUtility(id: Long, text: String) extends TweetUtilityTrait {
  import TweetResponseUtility._
  
  lazy val logger = LoggerFactory.getLogger(classOf[TweetResponseUtility])
  
  logger.debug("Class called") // TODO add this to configuration
  private val tweetId = id
  private val tweetText = text
  
  implicit def toOption = Some(this)
  private def twitterAPI = api.getClientAPI
  def getId = tweetId
  def getOurId = twitterAPI.getId
  def statusUpdate = new StatusUpdate(text)
  def send: Status = twitterAPI.updateStatus(statusUpdate.inReplyToStatusId(id))
  def getStatus: String = send.toString // TODO we need something here if send doesnt work or is none
  
}
