package models

/**
  * Created by vvass on 12/28/16.
  */


import datasources.TwitterAuthorization
import org.slf4j.LoggerFactory
import play.api.libs.json.JsValue
import twitter4j.{Status, StatusUpdate}


sealed trait TweetUtilityTrait {
  def toOption: Option[Any] // TODO we might need to modify this or get rid of it
//  def getStatus: String
  def getOurId: Long
  def statusUpdate: StatusUpdate
}

object TweetResponseUtility {
  
  /** This Authorizes the client and init a twitter object so that we can call
    * the twitter4j APIs
    */
  
  private def api = new TwitterAuthorization
  
}

class TweetResponseUtility(id: Long, screenName: String, text: String, responseText: JsValue) extends TweetUtilityTrait {
  import TweetResponseUtility._
  
  lazy val logger = LoggerFactory.getLogger(classOf[TweetResponseUtility])
  
  logger.debug("Class called") // TODO add this to configuration / Also make sure the val are declared in the param field above
  private val tweetId = id //710064201293811712L
  private val tweetText = text
  private val tweetScreenName = screenName
  private val tweetResponseText = (responseText \\ "response")map(_.as[JsValue])
  
  implicit def toOption = Some(this)
  private def twitterAPI = api.getClientAPI
  // TODO we need to add comments to these defs
  def getId = tweetId
  def getResponseText = tweetResponseText
  def getOurScreenName = tweetScreenName
  def getOurId = twitterAPI.getId
  def currentTweetId = twitterAPI.getHomeTimeline // Useful for get top tweets and the tweet id
  def statusUpdate = new StatusUpdate("@" + tweetScreenName + " " + tweetResponseText(0)) // first element found in the recursion(\\) of "response"
  def send: Status = twitterAPI.updateStatus(statusUpdate.inReplyToStatusId(id))
//  def getStatus: String = send.toString // TODO we need something here if send doesnt work or is none
  
}
