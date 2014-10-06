package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._
import java.util.{Date}

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


   def getTasksEndsAfter(taskowner: String, endsAfter: String) = Action
   {
      Global.dateQueryStringParse(endsAfter) match
      {
         case Left(error) => error
         case Right(date) =>
            if (User.Exists(taskowner))
            {
               Ok(Json.toJson (Task.tasksOfUserEndsAfter(taskowner, date)))
            }
            else
            {
               NotFound ("El usuario solicitado no existe")
            }
      }
   }


   def getTasksEndsBetween(taskowner: String, rangeBegin: String, rangeEnd: String) = Action
   {
      (Global.dateQueryStringParse(rangeBegin), Global.dateQueryStringParse(rangeEnd)) match
      {
         case (Left(error), _) => error
         case (Right(_), Left(error)) => error
         case (Right(dateBegin), Right(dateEnd)) =>
            if(dateBegin.after(dateEnd))
            {
               BadRequest("El fin de rango de fechas debe ser posterior al inicio del mismo")
            }
            else if (User.Exists(taskowner))
            {
               Ok(Json.toJson (Task.tasksOfUserEndsBetween(taskowner, dateBegin, dateEnd)))
            }
            else
            {
               NotFound ("El usuario solicitado no existe")
            }
      }
   }


   def getTasksWithoutDeadend(taskowner: String) = Action
   {
      if(User.Exists(taskowner))
      {
         Ok(Json.toJson(Task.tasksOfUserWithoutDeadend(taskowner)))
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