package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.json._

case class Task(id: Long, label: String, owner: User)

object Task
{
   /*Region acoplada a User*/


   /**
    * Crea una tarea con al descripción dada y cuyo propietario será el usuario dado (de existir)
    * @param label descripción de la tarea
    * @param taskowner propietario de la tarea
    * @return Option[Long]: Some[id] con el id de la tarea creada OR None si algo falló
    * @throws JdbcSQLException Si el usuario no existe (entre otros errores de BD)
    */
   def create(label: String, taskowner: String): Option[Long] = DB.withConnection
   {
      implicit c => SQL("insert into task (label, taskowner) values ({label}, {taskowner})").
         on('label -> label, 'taskowner -> taskowner).executeInsert()
   }


   /**
    * @return List[Task] con todas las tareas en la BD
    */
   def allOfUser(taskowner: String): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where taskowner = {taskowner}").on('taskowner -> taskowner).as(task *)
   }


   /*End Region acoplada a User*/


   /*Region Tareas anonimas*/


   /**
    * @return List[Task] con todas las tareas en la BD
    */
   def all(): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where taskowner = 'tasks'").as(task *)
   }


   /**
    * Crea una tarea con al descripción dada
    * @param label descripción de la tarea
    * @return Option[Long]: Some[id] con el id de la tarea creada OR None si algo falló
    */
   def create(label: String): Option[Long] = DB.withConnection
   {
      implicit c => SQL("insert into task (label) values ({label})").on('label -> label).executeInsert()
   }


   /**
    * Devuelve un Option[Task] del id dado
    * @param id id de la tarea a recuperar
    * @return Some(Task) con la tarea existente OR None si la tarea no existe
    */
   def read(id: Long): Option[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where id = {id}").on('id -> id).as(task singleOpt)
   }


   /*End Region Tareas anonimas*/


   /*Region Global*/


   /**
    * Borra la(s) tarea(s) con el Id dado
    * @param id id de la tarea a borrar
    * @return numero de tareas borradas (en principio '0' OR '1')
    */
   def delete(id: Long): Int = DB.withConnection
   {
      implicit c => SQL("delete from task where id = {id}").on('id -> id).executeUpdate()
   }


   /**
    * Conversor SQL a Task
    */
   val task =
   {
      get[Long]("id") ~ get[String]("label") ~ get[String]("taskowner") map
      {
         case id~label~taskowner => Task(id, label, User.read(taskowner))
      }
   }


   /**
    * Conversor Task a Json
    */
   implicit val taskWrites = new Writes[Task]
   {
      def writes(task: Task) = Json.obj(
         "id" -> task.id,
         "label" -> task.label,
         "owner" -> task.owner
      )
   }


   /*End Region Global*/
}