package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.json._

case class Task(id: Long, label: String)

object Task
{
   def all(): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task").as(task *)
   }


   def create(label: String)
   {
      DB.withConnection
      {
         implicit c => SQL("insert into task (label) values ({label})").on('label -> label).executeUpdate()
      }
   }


   def read(id: Long): Option[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where id = {id}").on('id -> id).as(task singleOpt)
   }


   def delete(id: Long)
   {
      DB.withConnection
      {
         implicit c => SQL("delete from task where id = {id}").on('id -> id).executeUpdate()
      }
   }


   val task =
   {
      get[Long]("id") ~ get[String]("label") map
      {
         case id~label => Task(id, label)
      }
   }

   implicit val taskWrites = new Writes[Task]
   {
      def writes(task: Task) = Json.obj(
         "id" -> task.id,
         "label" -> task.label
      )
   }
}