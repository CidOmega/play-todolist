package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._

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


   def createTaskId = Action
   {
      implicit request => taskForm.bindFromRequest.fold(
         errors => BadRequest("Datos incorrectos"),
         label =>
         {
            Task.create(label) match
            {
               case Some(idNewTask) => Created(Json.toJson(Task.read(idNewTask)))
               case None => InternalServerError("La tarea no se insertó por algun motivo desconocido")
            }
         }
      )
   }


   def createTaskWithOwner(taskowner: String) = Action
   {
      implicit request => taskForm.bindFromRequest.fold(
         errors => BadRequest("Datos incorrectos"),
         label =>
         {
            if (User.Exists(taskowner))
            {
               Task.create(label, taskowner) match
               {
                  case Some(idNewTask) => Created(Json.toJson(Task.read(idNewTask)))
                  case None => InternalServerError("La tarea no se insertó por algun motivo desconocido")
               }
            }
            else
            {
               NotFound("El usuario solicitado no existe")
            }
         }
      )
   }


   def deleteTaskId(id: Long) = Action
   {
      Task.delete(id) match
      {
         case 0 => NotFound("Tarea no encontrada")
         case _ => Ok("Tarea borrada, Id: " + id)
      }
   }


   val taskForm = Form("label" -> nonEmptyText)




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


   /*End Region UI*/
}