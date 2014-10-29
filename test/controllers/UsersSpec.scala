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
class UsersSpec extends Specification with JsonMatchers{

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

   val fechas : Array[Option[Date]] = Array(
      Some(new Timestamp(754182000000L)),//754182000000L == 25/11/1993
      Some(new Timestamp(785718000000L)),//785718000000L == 25/11/1994
      Some(new Timestamp(817254000000L)),//817254000000L == 25/11/1995
      Some(new Timestamp(848876400000L)),//848876400000L == 25/11/1996
      Some(new Timestamp(880412400000L)))//880412400000L == 25/11/1997

   val fechaFuruta = Some(new Timestamp(4130780400000L))//4130780400000L == 25/11/2100

   "Users GETs" should {

      "Devolver el User en formato Json al hacer GET /<nick existente>" in new WithApplication() {
         val home = route(FakeRequest(GET, "/" + userNicks(0))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         val json = Json.stringify(contentAsJson(home))

         json must /("nick" -> userNicks(0))
      }

      "Devolver 404 al hacer GET /<nick inexistente>" in new WithApplication() {
         val home = route(FakeRequest(GET, "/" + noUserNick)).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "Devolver la lista de tareas del User en formato Json al hacer GET /<nick existente>/tasks" in new WithApplication() {
         for(i <- 0 to 4)
            Task.create(labels(i), userNicks(0), fechas(i))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/tasks")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         val json = contentAsJson(home)

         json match {
            case arr: JsArray => arr.value.length === 5
            case _ => throw new Exception("json returned must be an array but it isn't")
         }

         var jsonString = Json.stringify(json)

         jsonString must /#(0) /("id" -> 1)
         jsonString must /#(1) /("id" -> 2)
         jsonString must /#(2) /("id" -> 3)
         jsonString must /#(3) /("id" -> 4)
         jsonString must /#(4) /("id" -> 5)
      }

      "Devolver 404 al hacer GET /<nick inexistente>/tasks" in new WithApplication() {
         val home = route(FakeRequest(GET, "/" + noUserNick + "/tasks")).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")
      }

   }

   "User Modificadores" should {

      "Devolver la nueva tarea en formato Json al hacer POST /<nick existente>/tasks y realmente crear la task" in new WithApplication() {
         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/tasks").
            withFormUrlEncodedBody(("label",labels(0)),("deadend","25/11/1993"))).get

         status(home) must equalTo(CREATED)
         contentType(home) must beSome.which(_ == "application/json")

         var jsonString = Json.stringify(contentAsJson(home))

         jsonString must /("id" -> (be_>(-1) ^^ ((_:String).toDouble.toInt)))
         jsonString must /("label" -> labels(0))
         jsonString must /("owner") /("nick" -> userNicks(0))
         jsonString must /("deadend" -> "25/11/1993")

         Task.readOption(1L) must beSome
      }

      "Devolver 404 al hacer POST /<nick inexistente>/tasks" in new WithApplication() {
         val home = route(FakeRequest(POST, "/" + noUserNick + "/tasks").
            withFormUrlEncodedBody(("label",labels(0)),("deadend","25/11/1993"))).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "Devolver 400 al hacer POST /<nick existente>/tasks con datos incorrectos" in new WithApplication() {
         var home = route(FakeRequest(POST, "/" + noUserNick + "/tasks").
            withFormUrlEncodedBody(("deadend","25/11/1993"))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")


         home = route(FakeRequest(POST, "/" + noUserNick + "/tasks").
            withFormUrlEncodedBody(("label",labels(0)),("deadend","1993/11/25"))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")


         home = route(FakeRequest(POST, "/" + noUserNick + "/tasks").
            withFormUrlEncodedBody(("label",labels(0)),("deadend","32/13/1993"))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "Devolver 400 al hacer DELETE /<nick existente>/tasks/outdate y realmente borrar las tasks" in new WithApplication() {
         var listaBorrados: List[Long] = Nil
         for(i <- 1 to 4)
            listaBorrados = Task.create(labels(i), userNicks(0), fechas(i)).get :: listaBorrados

         var tuplaNoBorrados = (Task.create(labels(4), userNicks(0), fechaFuruta).get, Task.create(labels(4), userNicks(0), None).get)

         val home = route(FakeRequest(DELETE, "/" + userNicks(0) + "/tasks/outdate")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "text/plain")

         for(j <- listaBorrados)
            Task.readOption(j) must beNone
         Task.readOption(tuplaNoBorrados._1) must beSome
         Task.readOption(tuplaNoBorrados._2) must beSome
      }

      "Devolver 404 al hacer DELETE /<nick inexistente>/tasks/outdate" in new WithApplication() {
         val home = route(FakeRequest(DELETE, "/" + noUserNick + "/tasks/outdate")).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")
      }

   }
}
