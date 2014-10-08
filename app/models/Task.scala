package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.json._

import java.util.{Date}

case class Task(id: Long, label: String, owner: User, deadend: Option[Date])

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
   def create(label: String, taskowner: String, deadend: Option[Date]): Option[Long] = DB.withConnection
   {
      implicit c => SQL("insert into task (label, taskowner, deadend) values ({label}, {taskowner}, {deadend})").
         on('label -> label, 'taskowner -> taskowner, 'deadend -> deadend).executeInsert()
   }


   /**
    * Devuelve las tareas de un usuario
    * @param taskowner propietario de las tareas a recuperar
    * @return List[Task] con todas las tareas en la BD del usuario dado
    */
   def allOfUser(taskowner: String): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where taskowner = {taskowner}").on('taskowner -> taskowner).as(task *)
   }


   /*Region con filtros de fecha*/


   def tasksOfUserEndsAfter(taskowner:String, endsAfter: Date): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where taskowner = {taskowner} and deadend >= {endsAfter}").
         on('taskowner -> taskowner, 'endsAfter -> endsAfter).as(task *)
   }


   def tasksOfUserEndsBetween(taskowner:String, dateBegin: Date, dateEnd: Date): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where taskowner = {taskowner} and deadend >= {dateBegin} and deadend <= {dateEnd}").
         on('taskowner -> taskowner, 'dateBegin -> dateBegin, 'dateEnd -> dateEnd).as(task *)
   }


   def tasksOfUserWithoutDeadend(taskowner:String): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where taskowner = {taskowner} and deadend is null").
         on('taskowner -> taskowner).as(task *)
   }


   /*End Region con filtros de fecha*/


   /*End Region acoplada a User*/


   /*Region Tareas anonimas*/


   /**
    * @return List[Task] con todas las tareas en la BD con usuario tasks (anonimas)
    */
   def allAnonimus(): List[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where taskowner = 'tasks'").as(task *)
   }


   /**
    * Crea una tarea con al descripción dada
    * @param label descripción de la tarea
    * @return Option[Long]: Some[id] con el id de la tarea creada OR None si algo falló
    */
   def create(label: String, deadend: Option[Date]): Option[Long] = DB.withConnection
   {
      implicit c => SQL("insert into task (label, deadend) values ({label}, {deadend})").
         on('label -> label, 'deadend -> deadend).executeInsert()
   }


   /*End Region Tareas anonimas*/


   /*Region Global*/


   /**
    * Devuelve un Option[Task] del id dado
    * @param id id de la tarea a recuperar
    * @return Some(Task) con la tarea existente OR None si la tarea no existe
    */
   def readOption(id: Long): Option[Task] = DB.withConnection
   {
      implicit c => SQL("select * from task where id = {id}").on('id -> id).as(task singleOpt)
   }


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
      get[Long]("id") ~ get[String]("label") ~ get[String]("taskowner") ~ get[Option[Date]]("deadend") map
      {
         case id~label~taskowner~deadend => Task(id, label, User.read(taskowner), deadend)
      }
   }


   /**
    * Conversor Task a Json
    */
   implicit val taskWrites = new Writes[Task]
   {
      val formater: java.text.SimpleDateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")

      def writes(task: Task) = Json.obj(
         "id" -> task.id,
         "label" -> task.label,
         "owner" -> task.owner,
         "deadend" -> JsString(task.deadend.map(formater.format(_)).getOrElse(null))
      )
   }


   /*End Region Global*/
}