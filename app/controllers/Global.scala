package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._
import java.util.{Date}

object Global extends Controller
{
   val taskForm = Form(mapping
      (
         "id" -> ignored(-1L),
         "label" -> nonEmptyText,
         "owner" -> ignored(User("Nadie")),
         "deadend" -> optional(date("dd/MM/yyyy")),
         "categories" -> list(mapping
            (
               "name" -> nonEmptyText,
               "owner" -> ignored(User("mismo que la tarea"))
            )(Category.apply)(Category.unapply))
      )(Task.apply)(Task.unapply))

   val dateQueryStringFormat = "dd-MM-yyyy"
   val dateQueryStringParser: java.text.SimpleDateFormat = new java.text.SimpleDateFormat(dateQueryStringFormat)
   def dateQueryStringParse(queryString: String): Either[Result, Date] =
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


   def Today: Date =
   {
      //Para borrar las horas y que coincida con lo esperado por la base de datos
      dateQueryStringParser.parse(dateQueryStringParser.format(new Date()))
   }


   def index = Action
   {
      Redirect(routes.Ui.ui_main)
   }
}