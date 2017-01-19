package controllers

import datasources.CouchbaseDatasourceObject
import models.TweetResponseUtility
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.utils.UriEncoding
import java.nio.charset.{StandardCharsets => SC}

/**
  * Created by vvass on 6/3/16.
  */
class CouchbaseQueryController extends Controller {
  
  
  val logger = LoggerFactory.getLogger(classOf[CouchbaseQueryController])

  /**
    * The connection to the cluster.
    */

  def getDocument = Action(parse.anyContent) { request =>

    val results = new StringBuilder

    /* We want to create a list of of words inside of the request so that we can
    ** query each one individually.
    */
    def requestList = request.queryString.getOrElse("primary",null)(0).toString.split(" +")

    /* We don't want to parse words that are less then 3 letters long. It is useless.
    ** This is where we parse out the string from the request in order to send it
    ** to the query for couchbase. Also this will make sure that only english text
    ** is processed.
    */

    for (word <- requestList if word.length > 3) { // TODO put this length in config
      if (word.matches("^[a-zA-Z0-9]*$")) // TODO put this in a config
        results ++= CouchbaseDatasourceObject.queryDocByString(word.replaceAll("\"", "")).toString
    }

    // TODO make sure primary has a check if null
    // TODO make sure primary has a check if not around
    if (results.isEmpty) {
      logger.debug("KO" + " None")
      Ok(Json.obj("status" -> "KO", "results" -> "None"))
    }
    else if(false) { // TODO we need this to be in a configuration
      logger.debug("OK"+results.toString)
      Ok(Json.obj("status" -> "OK", "results" -> Json.parse(results.toString())))
    }
    else {
      logger.debug("OK" + "Found Something") // TODO we need a configuration
      Ok(Json.obj("status" -> "OK", "results" -> "Found Something --> Processing"))
    }
  }

  def newGetDoc(id: Long, screenName: String, text: String) = Action {
    // TODO encrypt the id for twitter
    
    logger.debug("Entering oldGetDocument Twitter") // TODO this needs to be in configuration
  
    val results = new StringBuilder
    
    def requestList = text.toString.split(" +")
  
    /* We don't want to parse words that are less then 3 letters long. It is useless.
    ** This is where we parse out the string from the request in order to send it
    ** to the query for couchbase. Also this will make sure that only english text
    ** is processed.
    */
  
    for (word <- requestList if word.length > 3) { // TODO put this length in config
      if (word.matches("^[a-zA-Z0-9]*$")) // TODO put this in a config
        results ++= CouchbaseDatasourceObject.queryDocByString(word.replaceAll("\"", "")).toString
    }
  
    // TODO make sure primary has a check if null
    // TODO make sure primary has a check if not around
    if (results.isEmpty) {
      logger.debug("KO" + " Nothing found") // TODO we need a configuration
      Ok(Json.obj("status" -> "KO", "results" -> "None"))
    }
    else if(true) { // TODO we need this to be in a configuration
      logger.debug("OK" + results.toString) // TODO we need a configuration
      Ok(Json.obj("status" -> "OK", "screenname" -> screenName, "results" -> results.toString))
    }
    else {
      logger.debug("OK" + "Found Something") // TODO we need a configuration
      val responseText = Json.parse(results.toString())
      val responseAPI = new TweetResponseUtility(id, screenName, text, responseText) // TODO Need to be called once, maybe move to object
      // TODO we all need a way to handle exceptions if there is denial from Twitter
      responseAPI.send
      Ok(Json.obj("status" -> "OK", "screenname" -> screenName, "sent_screenname" -> responseAPI.getOurScreenName, "results" -> results.toString))
    }
  
  
    
    
  }

}
