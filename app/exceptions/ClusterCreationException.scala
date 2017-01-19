package exceptions

/**
  * Created by vvass on 6/3/16.
  */
case class ClusterCreationException() extends Exception(Messages.ClusterCreationException)
case class ClusterVarIsNull() extends Exception(Messages.ClusterVarIsNull)

object Messages {
  val ClusterCreationException: String = "Issue with creating coucbase cluster"
  val ClusterVarIsNull: String = "Cluster object is not initialized"
}
