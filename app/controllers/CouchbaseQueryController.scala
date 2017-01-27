package controllers

import java.net.{DatagramPacket, DatagramSocket, InetAddress, InetSocketAddress}
import java.nio.charset.{StandardCharsets => SC}
import javax.inject.Inject
import javax.smartcardio.ResponseAPDU

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.{IO, Udp}
import akka.stream.Materializer
import akka.util.ByteString
import com.google.inject.ImplementedBy
import configurations.CouchbaseQueryControllerConfiguration
import datasources.CouchbaseDatasourceObject
import exceptions.PacketLenghtOutOfBounds
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import services.TweetResponseUtility

case object Start

@ImplementedBy(classOf[CouchbaseQueryControllerImp])
trait CouchbaseQueryTrait {
  val inetAddress: String
  val socketPort: Int
  val broadcast: Boolean
  val listen: Boolean
  val bufferSize: Int
  val matchPattern: String
  val wordLength: Int
  val allowResponses: Boolean
}

class CouchbaseQueryControllerImp @Inject()(implicit system: ActorSystem, materializer: Materializer, config: CouchbaseQueryControllerConfiguration)
  extends Controller {
  
  val logger = LoggerFactory.getLogger(classOf[CouchbaseQueryControllerImp])
  
  /**
    * The connection to the cluster.
    */
  def getDocument = Action {
    val system = ActorSystem("example")
    val listener = system.actorOf(Props(new DataGramSocketListener(config)), "DataGramSocket")
    
    listener ! Start
    
    Ok("You have started couch client") // We need some nicer print out here in views
  }
}

class DataGramSocketListener @Inject()(config: CouchbaseQueryControllerConfiguration)
  extends Actor with CouchbaseQueryTrait {
  import models.Tweet
  
  val logger = LoggerFactory.getLogger(classOf[DataGramSocketListener])
  
  // -- Configs --
  override val inetAddress = this.config.inetAddress
  override val socketPort = this.config.socketPort
  override val broadcast = this.config.broadcast
  override val listen = this.config.listen
  override val bufferSize = this.config.bufferSize
  override val matchPattern = this.config.matchPattern
  override val wordLength = this.config.wordLength
  override val allowResponses = this.config.allowResponses
  // -- End of Configs --
  
  def receive = {
    case Start => {
      val socket = new DatagramSocket(socketPort, InetAddress.getByName(inetAddress))
      
      socket.setBroadcast(broadcast)
      while (listen) {
        
        val receiveBuffer = Array.fill(bufferSize){0.toByte}
        val packet = new DatagramPacket(receiveBuffer, receiveBuffer.length)
        socket.receive(packet)
  
        /**
          * This will throw an error if the amount of bytes returned exceed the amount
          * allocated for receiveBuffer
          */
        if (bufferSize < packet.getLength) { throw new PacketLenghtOutOfBounds }
        // TODO 2 send back notice to end service and close the connection. Tell scrubber to end
        
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
    def requestList = tweet.text.toString.toLowerCase.split(" +")
  
    for (word <- requestList if word.length > wordLength) {
      if (word.matches(matchPattern))
        results ++= CouchbaseDatasourceObject.queryDocByString(word.replaceAll("\"", "")).toString
    }
        
    if(!results.isEmpty) sendResponseToUser(tweet, results)
    
    // Clean up StringBuilder so we are not taking up space
    results.clear()
  }
  
  def sendResponseToUser(tweet: Tweet, results: StringBuilder) = {
    
    val responseAPI = new TweetResponseUtility(tweet, Json.parse(results.toString()))
  
    logger.info(responseAPI.getResponseText(0).toString())
  
    /**
      * This will send responses back to twitter from matching tweet id. The
      * response from couchbase will be the responding message.
      */
    if(allowResponses) responseAPI.send
    
    // Clean up StringBuilder so we are not taking up space
    results.clear()
  }
  
}

