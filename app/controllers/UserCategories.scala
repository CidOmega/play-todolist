package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._


object UserCategories extends Controller
{

   def getUserCategories(owner:String) = Action
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

   def getUserCategory(owner:String, categoryName: String) = Action
   {
      if(User.Exists(owner))
      {
         Category.readOption(categoryName, owner) match
         {
            case Some(c) => Ok(Json.toJson(c))
            case None => NotFound("Categoria no encontrada")
         }

      }
      else
      {
         NotFound("Usuario solicitado no encontrado")
      }
   }

   def getUserCategoryTasks(owner:String, categoryName: String) = Action
   {
      if(User.Exists(owner))
      {
         if(Category.exists(categoryName, owner))
         {
            Ok(Json.toJson(Task.tasksFromUserCategory(owner, categoryName)))
         }
         else
         {
            NotFound("Categoria no encontrada")
         }
      }
      else
      {
         NotFound("Usuario solicitado no encontrado")
      }
   }

   def addTaskToUserCategory(owner:String, categoryName: String) = Action
   {
      implicit request => Global.modifyTaskForm.bindFromRequest.fold(
         errors => BadRequest("Datos incorrectos"),
         task => {
            if (User.Exists(owner))
            {
               if (Category.exists(categoryName, owner))
               {
                  Task.readOption(task.id) match
                  {
                     case Some(dbTask) =>
                        if(dbTask.owner.nick != owner)
                        {
                           BadRequest("La tarea no es del usuario dueño de la categoria")
                        }
                        else if(dbTask.categories.contains(Category(categoryName, User(owner))))
                        {
                           BadRequest("La tarea ya pertenece a la categoría")
                        }
                        else
                        {
                           Category.addTaskToCategory(task.id, categoryName, owner)
                           Ok(Json.toJson(Task.readOption(task.id)))
                        }

                     case None => BadRequest("La tarea no existe")
                  }
               }
               else
               {
                  BadRequest("La categoria no existe")
               }
            }
            else
            {
               NotFound("Usuario solicitado no encontrado")
            }
         }
      )
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