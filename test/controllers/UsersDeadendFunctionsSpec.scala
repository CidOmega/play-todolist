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

import controllers.Global

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

      "GET /:owner/tasks/ends_at debe devolver una lista vacia si no existen tareas en la fecha dada" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechas(i))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_at?endsAt=" + "25-11-2000")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 0)
      }

      "GET /:owner/tasks/ends_at debe devolver una lista con las tareas con deadend en la fecha dada" in new WithApplication() {
         for(i <- 0 to 1)
         {
            Task.create(labels(i), userNicks(0), fechas(0))
            Task.create(labels(i), userNicks(0), fechas(1))//justo en el deadend, solo estas son devueltas
            Task.create(labels(i), userNicks(0), fechas(2))
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_at?endsAt=" + fechasString(1))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 2)
      }

      "GET /:owner/tasks/ends_before debe devolver una lista vacia si no existen tareas antes de la fecha dada" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechas(i))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_before?endsBefore=" + "25-11-1950")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 0)
      }

      "GET /:owner/tasks/ends_before debe devolver una lista con las tareas con deadend antes de la fecha dada" in new WithApplication() {
         for(i <- 0 to 1)
         {
            Task.create(labels(i), userNicks(0), fechas(0))
            Task.create(labels(i), userNicks(0), fechas(1))//justo en el deadend, no devueltas
            Task.create(labels(i), userNicks(0), fechas(2))
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_before?endsBefore=" + fechasString(1))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 2)
      }

      "GET /:owner/tasks/ends_between debe devolver una lista vacia si no existen tareas entre las fechas dadas" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechas(i))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_between?rangeBegin=" + "25-11-1950" + "&rangeEnd=" + "25-11-1960")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 0)
      }

      "GET /:owner/tasks/ends_between debe devolver una lista con las tareas con deadend entre las fechas dadas" in new WithApplication() {
         for(i <- 0 to 1)
         {
            Task.create(labels(i), userNicks(0), fechas(0))//fuera del rango, no devueltas
            Task.create(labels(i), userNicks(0), fechas(1))//justo en el rango, devueltas
            Task.create(labels(i), userNicks(0), fechas(2))
            Task.create(labels(i), userNicks(0), fechas(3))//justo en el rango, devueltas
            Task.create(labels(i), userNicks(0), fechas(4))//fuera del rango, no devueltas
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_between?rangeBegin=" + fechasString(1) + "&rangeEnd=" + fechasString(3))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 6)
      }
   }

   "Filtros con fecha sin parametros" should {

      "GET /:owner/tasks/ends_today debe devolver una lista vacia si no existen tareas a dia de hoy" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechas(i))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_today")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 0)
      }

      "GET /:owner/tasks/ends_today debe devolver una lista con las tareas con deadend entre las fechas dadas" in new WithApplication() {
         for(i <- 0 to 2)
         {
            Task.create(labels(i), userNicks(0), fechas(0))
            Task.create(labels(i), userNicks(0), fechas(1))
            Task.create(labels(i), userNicks(0), fechaFuruta)

            Task.create(labels(i), userNicks(0), Some(Global.Today))
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/ends_today")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 3)
      }

      "GET /:owner/tasks/outdate debe devolver una lista vacia si no existen tareas terminadas de fecha" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechaFuruta)

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/outdate")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 0)
      }

      "GET /:owner/tasks/outdate debe devolver una lista con las tareas con deadend entre las fechas dadas" in new WithApplication() {
         for(i <- 0 to 1)
         {
            Task.create(labels(i), userNicks(0), fechas(0))
            Task.create(labels(i), userNicks(0), fechas(1))
            Task.create(labels(i), userNicks(0), fechaFuruta)
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/outdate")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 4)
      }

      "GET /:owner/tasks/no_deadend debe devolver una lista vacia si no existen tareas sin fecha" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechas(i))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/no_deadend")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 0)
      }

      "GET /:owner/tasks/no_deadend debe devolver una lista con las tareas sin fecha" in new WithApplication() {
         for(i <- 0 to 1)
         {
            Task.create(labels(i), userNicks(0), fechas(0))
            Task.create(labels(i), userNicks(0), fechas(1))
            Task.create(labels(i), userNicks(0), fechaFuruta)

            Task.create(labels(i), userNicks(0), None)
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks/no_deadend")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")
         mustBeJsArrayAndHasLength(contentAsJson(home), 2)
      }

   }
}