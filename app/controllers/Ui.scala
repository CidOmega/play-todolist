package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._

object Ui extends Controller
{
   /**
    * @deprecated
    */
   def ui_main = Action
   {
      Ok(views.html.index(Task.allAnonimus(), Global.taskForm))
   }


   /**
    * @deprecated
    */
   def ui_newTask = Action
   {
      implicit request => Global.taskForm.bindFromRequest.fold(
         errors => BadRequest(views.html.index(Task.allAnonimus(), errors)),
         label =>
         {
            Task.create(label)
            Redirect(routes.Ui.ui_main)
         }
      )
   }


   /**
    * @deprecated
    */
   def ui_deleteTask(id: Long) = Action
   {
      Task.delete(id)
      Redirect(routes.Ui.ui_main)
   }
}