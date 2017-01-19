package datasources

import java.util.concurrent.TimeUnit

import com.couchbase.client.java.query.{AsyncN1qlQueryResult, N1qlQuery}
import com.couchbase.client.java.view.{ViewResult, ViewQuery}
import com.couchbase.client.java.{Bucket, AsyncBucket, CouchbaseCluster}
import com.google.inject.Singleton
import exceptions.{ClusterVarIsNull, ClusterCreationException}
import play.api.libs.json.Json
import play.api.{Play, Configuration}
import views.html.helper.select

import scala.collection.mutable.ArrayBuffer
import java.net.URI

/**
  * Created by vvass on 6/3/16.
  */

object CouchbaseDatasourceObject{

  /**
    * The instance of the client to connect to.
    */
  val cluster: CouchbaseCluster = CouchbaseCluster.create("127.0.0.1") // TODO needs to be configured

  val bucket: Bucket = cluster.openBucket("campaigns")

  val config: Configuration = play.api.Configuration()

  def connect() = {
    val hostname = config.getString("couchbase.bucket.host")
    val port = config.getString("couchbase.port")
    val databucket = config.getString("couchbase.bucket")
    val password = config.getString("couchbase.password")

    val uris = ArrayBuffer(URI.create("http://" + hostname + ":" + port + "/pools"))
  }

  def disconnect() = {
    if(cluster == null) (throw new ClusterVarIsNull)
    val timeout = config.getString("couchbase.shutdownTimeout").toString.toLong
    cluster.disconnect(timeout,TimeUnit.SECONDS)
  }

  def closeBucketOnly() = {
    if(cluster == null) (throw new ClusterVarIsNull)
    bucket.close()
  }

  def findDoc() = {
    val results  = bucket.get("1")
    println(results.content().toString)
    results
  }

  def queryDocByString(str: String) = {
    bucket.query(N1qlQuery.simple("CREATE PRIMARY INDEX ON `campaigns`"))
    val results = bucket.query(N1qlQuery.simple(s"SELECT * FROM `campaigns` WHERE primary_word = '$str'"))
    if(results.finalSuccess() && !results.allRows().isEmpty)
      results.allRows()
    else
      ""
  }

}

