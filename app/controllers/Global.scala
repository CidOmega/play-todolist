package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._

object Global extends Controller
{
   val taskForm = Form(mapping
      (
         "id" -> ignored(-1L),
         "label" -> nonEmptyText,
         "owner" -> ignored(User("Nadie")),
         "deadend" -> optional(date("dd/MM/yyyy"))
      )(Task.apply)(Task.unapply))


   def index = Action
   {
      Redirect(routes.Ui.ui_main)
   }
}