package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._

/**
 * @deprecated
 */
object Tasks extends Controller
{
   /**
   * @deprecated
   */
   def tasks = Action
   {
      Ok(Json.toJson(Task.all))
   }


   /**
    * @deprecated
    */
   def getTaskId(id: Long) = Action
   {
      Task.read(id) match
      {
         case Some(t) => Ok(Json.toJson(t))
         case None => NotFound("Tarea no encontrada")
      }
   }


   /**
    * @deprecated
    */
   def createTaskId = Action
   {
      implicit request => Global.taskForm.bindFromRequest.fold(
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


   def deleteTaskId(id: Long) = Action
   {
      Task.delete(id) match
      {
         case 0 => NotFound("Tarea no encontrada")
         case _ => Ok("Tarea borrada, Id: " + id)
      }
   }
}