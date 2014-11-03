package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._


object UserCategories extends Controller
{

   def getUserCategory(owner:String) = Action
   {
      if(User.Exists(owner))
      {
         Ok(Json.toJson(Category.categoriesFromUser(owner)))
      }
      else
      {
         NotFound("Usuario solicitado no encontrado")
      }
   }

   def createUserCategory(owner:String) = Action
   {
      implicit request => Global.categoryForm.bindFromRequest.fold(
         errors => BadRequest("Datos incorrectos"),
         category => {
            if (User.Exists(owner))
            {
               if (!Category.exists(category.name, owner))
               {
                  Category.create(category.name, owner) match
                  {
                     case 1 => Created(Json.toJson(Category.readOption(category.name, owner)))
                     case _ => InternalServerError("La categoria no se insertó por algun motivo desconocido")
                  }
               }
               else
               {
                  BadRequest("La categoria ya existe")
               }
            }
            else
            {
               NotFound("Usuario solicitado no encontrado")
            }
         }
      )
   }

}