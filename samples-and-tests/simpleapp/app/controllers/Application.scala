package controllers

import play.mvc._

object Application extends scalate.Controller  {
    
    def index(name: String = "Guest user") = render(name)
    
}

