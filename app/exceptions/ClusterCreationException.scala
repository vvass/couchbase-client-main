package exceptions

/**
  * Created by vvass on 6/3/16.
  */
case class ClusterCreationException() extends Exception(Messages.clusterCreationException)
case class ClusterVarIsNull() extends Exception(Messages.clusterVarIsNull)

object Messages{
  val clusterCreationException: String = "Issue with creating coucbase cluster"
  val clusterVarIsNull: String = "Cluster object is not initialized"
}
