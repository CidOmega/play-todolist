package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models.Task

object Application extends Controller
{
   def tasks = Action
   {
      Ok(Json.toJson(Task.all))
   }


   def getTaskId(id: Long) = Action
   {
      Task.read(id) match
      {
         case Some(t) => Ok(Json.toJson(t))
         case None => NotFound("Tarea no encontrada")
      }
   }


   //TODO Pendiente de redireccionar a otra pagina si las especificaciones no son las supuestas
   def index = Action
   {
      Redirect(routes.Application.ui_main)
   }


   /*Region UI*/


   def ui_main = Action
   {
      Ok(views.html.index(Task.all(), taskForm))
   }


   def ui_newTask = Action
   {
      implicit request => taskForm.bindFromRequest.fold(
            errors => BadRequest(views.html.index(Task.all(), errors)),
            label =>
            {
               Task.create(label)
               Redirect(routes.Application.ui_main)
            }
         )
   }


   def ui_deleteTask(id: Long) = Action
   {
      Task.delete(id)
      Redirect(routes.Application.ui_main)
   }


   val taskForm = Form("label" -> nonEmptyText)


   /*End Region UI*/
}