package configurations

import javax.inject.Inject
import play.api.Configuration

final class CouchbaseQueryControllerConfiguration @Inject()(config: Configuration) {
  
  //Main root location of config, application.conf file
  lazy val root = this.config
  //The filter object that will be used when listening to twitter stream
  lazy val couchbaseQueryClient = this.root.getConfig("couchbase.query.client").get
  
  lazy val inetAddress = this.couchbaseQueryClient.getString("inet-address").get
  lazy val socketPort = this.couchbaseQueryClient.getInt("socket-port").get
  lazy val broadcast = this.couchbaseQueryClient.getBoolean("broadcast").get
  lazy val listen = this.couchbaseQueryClient.getBoolean("listen").get
  lazy val bufferSize = this.couchbaseQueryClient.getInt("buffer-size").get
  lazy val matchPattern = this.couchbaseQueryClient.getString("match-pattern").get
  lazy val wordLength = this.couchbaseQueryClient.getInt("word-length-for-matching").get
  lazy val allowResponses = this.couchbaseQueryClient.getBoolean("allow-responses").get
  
}
