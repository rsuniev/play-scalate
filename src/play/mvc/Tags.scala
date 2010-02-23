package play.mvc
import scala.collection.JavaConversions._

object Tags {
  def url(action:String,m:Map[String,AnyRef]=Map()):String = {
    val im = collection.immutable.Map[String,AnyRef]()++m
    if (im.size == 0)  
      Router.reverse(action,new java.util.HashMap()).url
    else
      Router.reverse(action,im.asInstanceOf[java.util.Map[String,Object]]).url
  }
}
