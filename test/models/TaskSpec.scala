import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models._
import java.util.{Date}
import java.text.{SimpleDateFormat}

@RunWith(classOf[JUnitRunner])
class TaskSpec extends Specification {

   "Task" should {

      val parser = new SimpleDateFormat("dd/MM/yyyy")
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
         var ret = Task.create("Tarea de prueba", "edgar", Some(parser.parse("25/11/1993")))

         ret must beSome(be_>(-1L))

         addToListTasks(ret.get)
      }

      "Recuperar todos los datos de la tarea" in new WithApplication() {
         val label = "Tarea de prueba"
         val userNick = "edgar"
         val deadend = Some(parser.parse("25/11/1993"))

         var id = Task.create(label, userNick, deadend).get
         addToListTasks(id)

         Task.readOption(id) must beSome(Task(id ,label, User(userNick), deadend))
      }
      
   }
}
