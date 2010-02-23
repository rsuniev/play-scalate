package controllers

import play.mvc._
object Application extends ScalateController{
    
    def index(name: String = "Guest user") = renderScalate(name)
}

