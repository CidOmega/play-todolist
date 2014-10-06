package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._

object Users extends Controller
{
   def getTasks(taskowner: String) = Action
   {
      if(User.Exists(taskowner))
      {
         Ok(Json.toJson(Task.allOfUser(taskowner)))
      }
      else
      {
         NotFound("El usuario solicitado no existe")
      }
   }


   def createTask(taskowner: String) = Action
   {
      implicit request => Global.taskForm.bindFromRequest.fold(
         errors => BadRequest("Datos incorrectos"),
         task =>
         {
            if (User.Exists(taskowner))
            {
               Task.create(task.label, taskowner, task.deadend) match
               {
                  case Some(idNewTask) => Created(Json.toJson(Task.readOption(idNewTask)))
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
}