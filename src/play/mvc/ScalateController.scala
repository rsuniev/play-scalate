package play.mvc

 
abstract class ScalateController extends play.mvc.ScalaController with ScalateProvider {
  def renderScalate(args: Any*) {
    renderOrProvideTemplate(args.map(_.asInstanceOf[AnyRef])) match {
      case Some(template) => renderTemplate(template,args.map(_.asInstanceOf[AnyRef]))
      case None => 
    }
  }
}
