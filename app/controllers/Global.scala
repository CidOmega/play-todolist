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

   val dateQueryStringFormat = "dd-MM-yyyy"
   val dateQueryStringParser: java.text.SimpleDateFormat = new java.text.SimpleDateFormat(dateQueryStringFormat)
   def dateQueryStringParse(queryString: String): Either[Result, java.util.Date] =
   {
      try
      {
         Right(dateQueryStringParser.parse(queryString))
      }
      catch
      {
         case e: Exception => Left(BadRequest("Fecha '" + queryString + "' no parseable: " + e.toString + "\n\nFormato aceptable: " + dateQueryStringFormat))
      }
   }


   def index = Action
   {
      Redirect(routes.Ui.ui_main)
   }
}