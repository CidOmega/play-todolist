import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models._
import java.util.{Date}
import java.sql.{Timestamp} //Esto por listillo (aunque el unico problema era el formato de salida...)

@RunWith(classOf[JUnitRunner])
class TaskSpec extends Specification {


   val labels = Array(
            "Tarea de prueba",
            "Tarea de prueba2",
            "Tarea de prueba3",
            "Tarea de prueba4",
            "")

   val userNicks = Array(
            "edgar",
            "domingo")
   val anonymusNick = "tasks"

   val fechas : Array[Option[Date]] = Array(
      Some(new Timestamp(754182000000L)),//754182000000L == 25/11/1993
      Some(new Timestamp(785718000000L)),//785718000000L == 25/11/1994
      Some(new Timestamp(817254000000L)),//817254000000L == 25/11/1995
      Some(new Timestamp(848876400000L)),//848876400000L == 25/11/1996
      Some(new Timestamp(880412400000L)))//880412400000L == 25/11/1997


   "Task sobre tareas de usuarios" should {

      "Crear tareas correctamente" in new WithApplication{
         var ret = Task.create(labels(0), userNicks(0), fechas(0))

         ret must beSome(be_>(-1L))
      }

      "Recuperar todos los datos de la tarea existente y None de la no existente" in new WithApplication() {

         var id = Task.create(labels(0), userNicks(0), fechas(0)).get

         Task.readOption(id) must beSome(Task(id ,labels(0), User(userNicks(0)), fechas(0)))
         Task.readOption(-1L) must beNone
      }

      "Borrar las tareas correctamente" in new WithApplication() {

         var id = Task.create(labels(0), userNicks(0), fechas(0)).get

         Task.readOption(id) must beSome
         Task.delete(id) === 1
         Task.readOption(id) must beNone
         Task.delete(id) === 0

         Task.delete(-1L) === 0
      }

      "Borrar las tareas anteriores a cierta fecha correctamente" in new WithApplication() {

         Task.create(labels(0), userNicks(0), fechas(0))
         Task.create(labels(1), userNicks(0), fechas(1))
         Task.create(labels(2), userNicks(0), fechas(2))
         Task.create(labels(3), userNicks(0), fechas(3))

         Task.deleteTasksOfUserEndsBefore(userNicks(0), fechas(2).get) === 2

         val tasks = Task.allOfUser(userNicks(0)).toArray

         tasks.length === 2
         //No se como hacer que ignore el id
         tasks(0) === Task(tasks(0).id, labels(2), User(userNicks(0)), fechas(2))
         tasks(1) === Task(tasks(1).id, labels(3), User(userNicks(0)), fechas(3))
         tasks(0).id !== tasks(1).id
      }

      "Recuperar bien todas las tareas de un usuario" in new WithApplication() {

         Task.create(labels(0), userNicks(0), fechas(0))
         Task.create(labels(1), userNicks(0), fechas(0))
         Task.create(labels(2), fechas(0))
         Task.create(labels(3), userNicks(1), fechas(0))

         val tasks = Task.allOfUser(userNicks(0)).toArray

         tasks.length === 2
         //No se como hacer que ignore el id
         tasks(0) === Task(tasks(0).id, labels(0), User(userNicks(0)), fechas(0))
         tasks(1) === Task(tasks(1).id, labels(1), User(userNicks(0)), fechas(0))
         tasks(0).id !== tasks(1).id
      }

   }

   "Task sobre filtros de deadend" should {

      "Devolver solo tareas sin deadend" in new WithApplication() {

         Task.create(labels(0), userNicks(0), None)
         Task.create(labels(1), userNicks(0), None)
         Task.create(labels(2), userNicks(0), fechas(0))//Con fecha del mismo usuario, no debe recuperarse
         Task.create(labels(3), userNicks(1), None)//Sin fecha pero de otro user, no debe recuperarse

         val tasks = Task.tasksOfUserWithoutDeadend(userNicks(0)).toArray

         tasks.length === 2
         //No se como hacer que ignore el id
         tasks(0) === Task(tasks(0).id, labels(0), User(userNicks(0)), None)
         tasks(1) === Task(tasks(1).id, labels(1), User(userNicks(0)), None)
         tasks(0).id !== tasks(1).id
      }

      "Devolver solo tareas con deadend incluido en el rango dado" in new WithApplication() {

         Task.create(labels(0), userNicks(0), fechas(0))
         Task.create(labels(1), userNicks(0), fechas(1))
         Task.create(labels(2), userNicks(0), fechas(2))
         Task.create(labels(3), userNicks(0), fechas(3))
         Task.create(labels(4), userNicks(0), fechas(4))
         Task.create(labels(0), userNicks(0), None) //Tarea sin fecha, no se debuelve
         Task.create(labels(0), userNicks(1), fechas(2)) //Tarea en el rango, pero de otro user, no se debuelve

         val tasks = Task.tasksOfUserEndsBetween(userNicks(0), fechas(1).get, fechas(3).get).toArray

         tasks.length === 3
         //No se como hacer que ignore el id
         tasks(0) === Task(tasks(0).id, labels(1), User(userNicks(0)), fechas(1))
         tasks(1) === Task(tasks(1).id, labels(2), User(userNicks(0)), fechas(2))
         tasks(2) === Task(tasks(2).id, labels(3), User(userNicks(0)), fechas(3))
         tasks(0).id !== tasks(1).id
         tasks(0).id !== tasks(2).id
         tasks(1).id !== tasks(2).id
      }

      "Devolver solo tareas con deadend anterior a la fecha dada" in new WithApplication() {

         Task.create(labels(0), userNicks(0), fechas(0))
         Task.create(labels(1), userNicks(0), fechas(1))
         Task.create(labels(2), userNicks(0), fechas(2))//Tarea justo en la fecha, no se devuelve
         Task.create(labels(3), userNicks(0), fechas(3))
         Task.create(labels(4), userNicks(0), None) //Tarea sin fecha, no se debuelve
         Task.create(labels(0), userNicks(1), fechas(1)) //Tarea en fecha devolvible, pero de otro user, no se debuelve

         val tasks = Task.tasksOfUserEndsBefore(userNicks(0), fechas(2).get).toArray

         tasks.length === 2
         //No se como hacer que ignore el id
         tasks(0) === Task(tasks(0).id, labels(0), User(userNicks(0)), fechas(0))
         tasks(1) === Task(tasks(1).id, labels(1), User(userNicks(0)), fechas(1))
         tasks(0).id !== tasks(1).id
      }

      "Devolver solo tareas con deadend posterior a la fecha dada" in new WithApplication() {

         Task.create(labels(0), userNicks(0), fechas(0))
         Task.create(labels(1), userNicks(0), fechas(1))//Tarea justo en la fecha, no se devuelve
         Task.create(labels(2), userNicks(0), fechas(2))
         Task.create(labels(3), userNicks(0), fechas(3))
         Task.create(labels(4), userNicks(0), None) //Tarea sin fecha, no se debuelve
         Task.create(labels(0), userNicks(1), fechas(2)) //Tarea en fecha devolvible, pero de otro user, no se debuelve

         val tasks = Task.tasksOfUserEndsAfter(userNicks(0), fechas(1).get).toArray

         tasks.length === 2
         //No se como hacer que ignore el id
         tasks(0) === Task(tasks(0).id, labels(2), User(userNicks(0)), fechas(2))
         tasks(1) === Task(tasks(1).id, labels(3), User(userNicks(0)), fechas(3))
         tasks(0).id !== tasks(1).id
      }

   }

   "Task sobre tareas anonimas" should {

      "Crear tareas anonimas correctamente" in new WithApplication{
         var ret = Task.create(labels(0), fechas(0))

         ret must beSome(be_>(-1L))
      }

      "Recuperar todos los datos de la tarea existente y None de la no existente" in new WithApplication() {

         var id = Task.create(labels(0), fechas(0)).get

         Task.readOption(id) must beSome(Task(id ,labels(0), User(anonymusNick), fechas(0)))
         Task.readOption(-1L) must beNone
      }

      "Recuperar bien las tareas anonimas (allAnonimus)" in new WithApplication() {

         Task.create(labels(0), fechas(0))
         Task.create(labels(1), fechas(0))
         Task.create(labels(2), userNicks(0), fechas(0))

         val tasks = Task.allAnonimus.toArray

         tasks.length === 2
         //No se como hacer que ignore el id
         tasks(0) === Task(tasks(0).id, labels(0), User(anonymusNick), fechas(0))
         tasks(1) === Task(tasks(1).id, labels(1), User(anonymusNick), fechas(0))
         tasks(0).id !== tasks(1).id
      }

   }
}
