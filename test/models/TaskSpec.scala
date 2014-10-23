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

   "Task" should {

      var listTasks : List[Long] = Nil

      def before: Unit =
      {
         listTasks = Nil
      }

      def addToListTasks(id: Long): Unit =
      {
         listTasks = id :: listTasks
      }

      def after: Unit =
      {
         listTasks.foreach{ id => Task.delete(id) }
      }

      "Crear tareas correctamente" in new WithApplication{
         var ret = Task.create("Tarea de prueba", "edgar", Some(new Timestamp(754182000000L)))

         ret must beSome(be_>(-1L))

         addToListTasks(ret.get)
      }

      "Recuperar todos los datos de la tarea existente y None de la no existente" in new WithApplication() {
         val label = "Tarea de prueba"
         val userNick = "edgar"
         val deadend : Option[Date] = Some(new Timestamp(754182000000L))//754182000000L == 25/11/1993

         var id = Task.create(label, userNick, deadend).get
         addToListTasks(id)

         Task.readOption(id) must beSome(Task(id ,label, User(userNick), deadend))
         Task.readOption(-1L) must beNone
      }

      "Borrar las tareas correctamente" in new WithApplication() {
         val label = "Tarea de prueba"
         val userNick = "edgar"
         val deadend : Option[Date] = Some(new Timestamp(754182000000L))//754182000000L == 25/11/1993

         var id = Task.create(label, userNick, deadend).get
         //addToListTasks(id) //No necesario porque se borra en el propio test

         Task.readOption(id) must beSome
         Task.delete(id) === 1
         Task.readOption(id) must beNone
         Task.delete(id) === 0

         Task.delete(-1L) === 0
      }

   }
}
