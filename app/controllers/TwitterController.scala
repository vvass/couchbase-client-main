package controllers

import datasources.TwitterAuthorization
import models.TweetResponseUtility
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import twitter4j.{Query, QueryResult}

class TwitterController extends Controller {
  
  lazy val logger = LoggerFactory.getLogger(classOf[TwitterController])

  def index = Action { // TODO this needs to go
    Ok("Your new application is ready. ")
  }
  
  def postResponse(id: Long, text: String) = Action {
    
    // TODO encrypt the id for twitter
  
    logger.debug("Entering response Twitter") // TODO this needs to be in configuration
  
//    val responseAPI = new TweetResponseUtility(id,text) // TODO Need to be called once, maybe move to object
//    // TODO we all need a way to handle exceptions if there is denial from Twitter
//
//    responseAPI.send
      
    Ok(  "Yo")
    
    
  }
  

}
