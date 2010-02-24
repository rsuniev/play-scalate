package play.mvc

import play.classloading.enhancers.LocalvariablesNamesEnhancer.LocalVariablesNamesTracer
import play.Play
import play.exceptions.{UnexpectedException,PlayException,TemplateNotFoundException}
import play.data.validation.Validation
import org.fusesource.scalate._
import java.io.{StringWriter,PrintWriter}
import scala.collection.JavaConversions._
import java.io.File
import org.fusesource.scalate.util.SourceCodeHelper


private[mvc] trait ScalateProvider  {

  def init:TemplateEngine = {
    val engine = new TemplateEngine
    engine.workingDirectory = new File(System.getProperty("java.io.tmpdir"), "scalate")
    engine.bindings = List(
      Binding("session", SourceCodeHelper.name(classOf[Scope.Session])),
      Binding("request", SourceCodeHelper.name(classOf[Http.Request])),
      Binding("flash", SourceCodeHelper.name(classOf[Scope.Flash])),
      Binding("params", SourceCodeHelper.name(classOf[Scope.Params]))
    )
    if (Play.mode == Play.Mode.PROD) {
      engine.allowReload = false
    }
    engine.resourceLoader = new FileResourceLoader(Some(new File(Play.applicationPath+"/app/views")))
    engine
  }
  val engine = init
  // Create and configure the Scalate template engine
  
  def renderOrProvideTemplate(args:Seq[AnyRef]):Option[String] = {
    //determine template
    val templateName:String =
        if (args.length > 0 && args(0).isInstanceOf[String] && 
            LocalVariablesNamesTracer.getAllLocalVariableNames(args(0)).isEmpty) {
            discardLeadingAt(args(0).toString)
        } else {
          determineURI()
        }

    if (shouldRenderWithScalate(templateName)) {
      renderScalateTemplate(templateName,args)
      None
    } else {
      Some(templateName)
    }  

  }

  //determine if we need to render with scalate
  def shouldRenderWithScalate(template:String):Boolean = {
    val ignore = Play.configuration.getProperty("scalate.ignore") 
    if (Play.configuration.containsKey("scalate")) {
      if (ignore != null) {
         ignore.split(",").filter(template.startsWith(_)).size == 0
      } else true
    } else false 
  }

  //render with scalate
  def renderScalateTemplate(templateName:String, args:Seq[AnyRef]) = {
    val renderMode = Play.configuration.getProperty("scalate") 
    //loading template
    val lb = new scala.collection.mutable.ListBuffer[Binding]
    val buffer = new StringWriter()
    var context = new DefaultRenderContext(engine, new PrintWriter(buffer))
    val templateBinding = Scope.RenderArgs.current()
    
    // try to fill context
    for (o <-args) {
      for (name <-LocalVariablesNamesTracer.getAllLocalVariableNames(o).iterator) {
        context.attributes += name -> o
        lb += Binding(name,SourceCodeHelper.name(o.getClass))
      }
    }
    context.attributes += "session" -> Scope.Session.current.get
    context.attributes += "request" -> Http.Request.current.get
    context.attributes += "flash" -> Scope.Flash.current()
    context.attributes += "params" ->  Scope.Params.current.get
    try {
       context.attributes +="errors" -> Validation.errors()
    } catch { case ex:Exception => throw new UnexpectedException(ex)}
    
    try {
          val templatePath = templateName.replaceAll(".html","."+renderMode)
          val template = engine.load(templatePath, lb.toList)
          template.render(context)
          throw new ScalateResult(buffer.toString,templateName)
    } catch { 
        case ex:TemplateNotFoundException => {
          if(ex.isSourceAvailable) {
            throw ex
          }
          val element = PlayException.getInterestingStrackTraceElement(ex)
          if (element != null) {
             throw new TemplateNotFoundException(templateName, Play.classes.getApplicationClass(element.getClassName()), element.getLineNumber());
          } else {
             throw ex
          }
       }
    }
  }
  
  def discardLeadingAt(templateName:String):String = {
        if(templateName.startsWith("@")) {
            if(!templateName.contains(".")) {
                determineURI(Http.Request.current().controller + "." + templateName.substring(1))
            }
            determineURI(templateName.substring(1))
        } else templateName
  }

  def determineURI(template:String = Http.Request.current().action):String = {
     template.replace(".", "/") + "." + 
     (if (Http.Request.current().format == null)  "html" else Http.Request.current().format)
  }
}
private[mvc] object ScalateProvider extends ScalateProvider

