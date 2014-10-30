import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.{JsResultException, Json, JsValue, JsArray}

import models._
import java.util.{Date}
import java.sql.{Timestamp}


@RunWith(classOf[JUnitRunner])
class UsersDeadendFunctionsSpec extends Specification with JsonMatchers {

   val labels = Array(
      "Tarea de prueba",
      "Tarea de prueba2",
      "Tarea de prueba3",
      "Tarea de prueba4",
      "")
   val noUserNick = "noexisto"
   val userNicks = Array(
      "edgar",
      "domingo")

   val fechas: Array[Option[Date]] = Array(
      Some(new Timestamp(754182000000L)), //754182000000L == 25/11/1993
      Some(new Timestamp(785718000000L)), //785718000000L == 25/11/1994
      Some(new Timestamp(817254000000L)), //817254000000L == 25/11/1995
      Some(new Timestamp(848876400000L)), //848876400000L == 25/11/1996
      Some(new Timestamp(880412400000L))) //880412400000L == 25/11/1997

   val fechasString: Array[String] = Array(
      "25-11-1993",
      "25-11-1994",
      "25-11-1995",
      "25-11-1996",
      "25-11-1997")

   val fechaFuruta = Some(new Timestamp(4130780400000L)) //4130780400000L == 25/11/2100

   def mustBeJsArrayAndHasLength(json:JsValue, expected: Int): Unit =
   {
      json match {
         case arr: JsArray => arr.value.length === expected
         case _ => throw new Exception("JsValue must be an array but it isn't")
      }
   }

   "Funciones que obtienen tareas en rangos (ya sea dandole uno o ambos limites)" should{

      "GET /:owner/tasks/ends_after debe devolver una lista vacia si no existen tareas tras la fecha dada" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechas(i))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_after?endsAfter=" + "25-11-2000")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 0)
      }

      "GET /:owner/tasks/ends_after debe devolver una lista con las tareas con deadend tras la fecha dada" in new WithApplication() {
         for(i <- 0 to 1)
         {
            Task.create(labels(i), userNicks(0), fechas(0))
            Task.create(labels(i), userNicks(0), fechas(1))//justo en el deadend, no devueltas
            Task.create(labels(i), userNicks(0), fechas(2))
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_after?endsAfter=" + fechasString(1))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 2)
      }

   }
}