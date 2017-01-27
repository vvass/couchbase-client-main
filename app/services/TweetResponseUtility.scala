package services

/**
  * Created by vvass on 12/28/16.
  */

import models.Tweet
import org.slf4j.LoggerFactory
import play.api.libs.json.JsValue
import twitter4j.{Status, StatusUpdate}


sealed trait TweetUtilityTrait {
  def getOurId: Long
  def statusUpdate: StatusUpdate
}

object TweetResponseUtility {
  
  /** This Authorizes the client and init a twitter object so that we can call
    * the twitter4j APIs
    */
  
  private def api = new TwitterAuthorization
  
}

class TweetResponseUtility(tweet: Tweet, responseText: JsValue) extends TweetUtilityTrait {
  import TweetResponseUtility._
  
  lazy val logger = LoggerFactory.getLogger(classOf[TweetResponseUtility])
  
  logger.debug("TweetResponseUtility is Initialized ")
  
  implicit def toOption = Some(this)
  private def twitterAPI = api.getClientAPI
  
  /**
    * Retrieves the Tweet model id in scope
    */
  def getId = tweet.id
  
  /**
    * Get the response message set in couchbase server after a request has
    * been made from couchbase client. ResponseText is the json response from
    * couchbase client
    */
  def getResponseText = (responseText \\ "response")map(_.as[String])
  
  /**
    * Get the screen name of the original tweet. This is useful if you want
    * to respond directly to the user and need the screen name.
    */
  def getOurScreenName = tweet.user
  
  /**
    * Get the ID for our twitter app. Not the ID of the tweet.
    */
  def getOurId = twitterAPI.getId
  
  /**
    * Current Tweet ID captured during streaming. This is the tweet id which
    * is useful for messaging back the tweet directly or the person.
    */
  def currentTweetId = twitterAPI.getHomeTimeline // Useful for get top tweets and the tweet id
  
  /**
    * This is the main way to initialize an object that we use to respond back
    * to the tweet entry. This takes the responseText object from couchbase client
    * and the user screenname (@ is missing). This is tied directly with the send
    * definition.
    */
  def statusUpdate = new StatusUpdate("@" + tweet.user + " " + getResponseText(0)) // first element found in the recursion(\\) of "response"
  
  /**
    * This will send a response built out from statusUpdate. The response is
    * built on top of Twitter4j API. This is the main way to talk to Twitter at
    * the moment.
    */
  def send: Status = twitterAPI.updateStatus(statusUpdate.inReplyToStatusId(tweet.id))
  // TODO 2 we all need a way to handle exceptions if there is denial from Twitter
  
}
