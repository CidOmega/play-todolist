package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.json._

case class Category(name: String, owner: User)

object Category
{

   def create(name: String, owner: String) : Int = DB.withConnection
   {
      implicit c => SQL("insert into category (name, owner) values ({name}, {owner})").
         on('name -> name, 'owner -> owner).executeUpdate()
   }

   def readOption(name: String, owner: String): Option[Category] = DB.withConnection
   {
      implicit c => SQL("select * from category where name = {name} and owner = {owner}").
         on('name -> name, 'owner -> owner).as(category singleOpt)
   }

   def categoriesFromTask(task_id: Long): List[Category] = DB.withConnection
   {
      implicit c => SQL("select * from category, category_task where category_owner = owner and category_name = name and task_id = {task_id}").
         on('task_id -> task_id).as(category *)
   }

   def exists(name: String, owner: String) : Boolean =
   {
      readOption(name, owner) match
      {
         case Some(_) => true
         case None => false
      }
   }

   def addTaskToCategory(task_id: Long, category_name: String, category_owner: String) = DB.withConnection
   {
      implicit c => SQL("insert into category_task (category_name, category_owner, task_id) values ({category_name}, {category_owner}, {task_id})").
         on('category_name -> category_name, 'category_owner -> category_owner, 'task_id -> task_id).executeUpdate()
   }

   /**
    * Conversor SQL a Category
    */
   val category =
   {
      get[String]("name") ~ get[String]("owner") map
         {
            case name~owner => Category(name, User.readOption(owner).get) //Si falla el Option.get es que algo ha ido mal
         }
   }


   /**
    * Conversor Category a Json
    */
   implicit val categoryWrites = new Writes[Category]
   {
      def writes(category: Category) = Json.obj(
         "name" -> category.name
         //No queremos los datos del usuario
      )
   }
}