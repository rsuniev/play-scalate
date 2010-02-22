package play.mvc.scalate

import play.mvc.ScalaController

class Controller extends play.mvc.ScalaController {
  override def render(args: Any*) {
    play.mvc.ScalateSupport.renderOrProvideTemplate(args.map(_.asInstanceOf[AnyRef])) match {
      case Some(template) => renderTemplate(template,args.map(_.asInstanceOf[AnyRef]))
      case None => 
    }
  }
}
