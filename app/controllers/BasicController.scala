package controllers

import play.api.mvc.{Action, Controller}

class BasicController extends Controller{

  def index = Action {
    Ok("Your new application is ready.")
  }

  def echo = Action(parse.anyContent) { request =>
    Ok("Got request [" + request + "]")
  }

}
