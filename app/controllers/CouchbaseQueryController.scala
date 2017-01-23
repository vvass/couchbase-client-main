package controllers

import java.net.{DatagramPacket, DatagramSocket, InetAddress, InetSocketAddress}
import java.nio.charset.{StandardCharsets => SC}
import javax.inject.Inject
import javax.smartcardio.ResponseAPDU

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.{IO, Udp}
import akka.stream.Materializer
import akka.util.ByteString
import datasources.CouchbaseDatasourceObject
import exceptions.PacketLenghtOutOfBounds
import models.TweetResponseUtility
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}

case object Start

class CouchbaseQueryController @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {
  
  val logger = LoggerFactory.getLogger(classOf[CouchbaseQueryController])
  
  /**
    * The connection to the cluster.
    */
  def getDocument = Action {
    val address = new InetSocketAddress("localhost", 0)
    val system = ActorSystem("example")
    val listener = system.actorOf(Props[DataGramSocketListener], "DataGramSocket")
    
    listener ! Start
    
    Ok("You have started couch client") // We need some nicer print out here in views
  }
}

class DataGramSocketListener extends Actor {
  
  import models.Tweet
  
  def receive = {
    case Start => {
      val socket = new DatagramSocket(8136, InetAddress.getByName("127.0.0.1")) // TODO configure
      
      socket.setBroadcast(true)
      while (true) {
        
        val bufferSize = 500 // TODO add to con figuration
        
        val receiveBuffer = Array.fill(bufferSize){0.toByte}
        val packet = new DatagramPacket(receiveBuffer, receiveBuffer.length)
        socket.receive(packet)
  
        /**
          * This will throw an error if the amount of bytes returned exceed the amount
          * allocated for receiveBuffer
          */
        if (bufferSize < packet.getLength) { throw new PacketLenghtOutOfBounds }
        // TODO send back notice to end service and close the connection. Tell scrubber to end
        
        /**
          * There is extra bytes in the packet space. We originally designated by bufferSize.
          * This code makes sure we strip the extra content according to the length of the
          * packet. That way when convert to JSON it will not have /0000u bytes at the end.
          */
        val str: String = new java.lang.String(packet.getData(), packet.getOffset, packet.getLength)
        
        val packetAsJson: JsValue = Json.parse(str)
        queryCouchbaseServer(packetAsJson)
        
      }
  
    }
  }
  
  def queryCouchbaseServer(json: JsValue) = {
    
    val tweet = Tweet(
      (json \ "id_str").as[Long],
      (json \ "text").as[String],
        (json \ "screen_name").as[String]
    )
    
    val results = new StringBuilder
  
    // Split up tokens by white space
    def requestList = tweet.text.toString.split(" +")
  
    for (word <- requestList if word.length > 3) { // TODO put this length in config
      if (word.matches("^[a-zA-Z0-9]*$")) // TODO put this in a config
        results ++= CouchbaseDatasourceObject.queryDocByString(word.replaceAll("\"", "")).toString
    }
        
    if(!results.isEmpty) sendResponseToUser(tweet, results)
    
    // Clean up StringBuilder so we are not taking up space
    results.clear()
  }
  
  def sendResponseToUser(tweet: Tweet, results: StringBuilder) = {
    
    val responseAPI = new TweetResponseUtility(tweet, Json.parse(results.toString())) // TODO Need to be called once, maybe move to object
    // TODO we all need a way to handle exceptions if there is denial from Twitter
  
    Logger.info(responseAPI.getResponseText.toString())
  
    if(false) responseAPI.send // TODO add config
    
    // Clean up StringBuilder so we are not taking up space
    results.clear()
  }
  
}

