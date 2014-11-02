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
class TasksSpcec extends Specification with JsonMatchers{

   val labels = Array(
      "Tarea de prueba",
      "Tarea de prueba2",
      "Tarea de prueba3",
      "Tarea de prueba4",
      "")
   val userNicks = Array(
      "edgar",
      "domingo")

   val fechas : Array[Option[Date]] = Array(
      Some(new Timestamp(754182000000L)),//754182000000L == 25/11/1993
      Some(new Timestamp(785718000000L)),//785718000000L == 25/11/1994
      Some(new Timestamp(817254000000L)),//817254000000L == 25/11/1995
      Some(new Timestamp(848876400000L)),//848876400000L == 25/11/1996
      Some(new Timestamp(880412400000L)))//880412400000L == 25/11/1997

   val fechaFuruta = Some(new Timestamp(4130780400000L))//4130780400000L == 25/11/2100

   "Tasks" should {

      "Devolver un json con los datos de la tarea" in new WithApplication() {
         val Some(id) = Task.create(labels(0), userNicks(0), fechas(0))

         val home = route(FakeRequest(GET, "/tasks/" + id)).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         val json = Json.stringify(contentAsJson(home))

         json must /("id" -> id)
         json must /("owner") /("nick" -> userNicks(0))
         json must /("label" -> labels(0))
      }

      "Devolver 404 si la tarea no existe" in new WithApplication() {
         val Some(id) = Task.create(labels(0), userNicks(0), fechas(0))

         val home = route(FakeRequest(GET, "/tasks/" + (id + 100))).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "Devolver un texto de verificacion con el id de la tarea borrada" in new WithApplication() {
         var idNoBorrado: Array[Long] = new Array[Long](2);
         idNoBorrado(0) = Task.create(labels(0), userNicks(0), fechas(0)).get
         val Some(id) = Task.create(labels(0), userNicks(0), fechas(0))
         idNoBorrado(1) = Task.create(labels(0), userNicks(0), fechas(0)).get

         val home = route(FakeRequest(DELETE, "/tasks/" + id)).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "text/plain")

         contentAsString(home) must endWith("" + id)

         Task.readOption(idNoBorrado(0)) must beSome
         Task.readOption(id) must beNone
         Task.readOption(idNoBorrado(1)) must beSome
      }

      "Devolver 404 si la tarea no existe" in new WithApplication() {
         val Some(id) = Task.create(labels(0), userNicks(0), fechas(0))

         val home = route(FakeRequest(DELETE, "/tasks/" + (id + 100))).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")

         Task.readOption(id) must beSome
      }

   }
}