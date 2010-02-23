package play.mvc

 
abstract class ScalateController extends play.mvc.ScalaController {
  def renderScalate(args: Any*) {
    ScalateProvider.renderOrProvideTemplate(args.map(_.asInstanceOf[AnyRef])) match {
      case Some(template) => renderTemplate(template,args.map(_.asInstanceOf[AnyRef]))
      case None => 
    }
  }
}
