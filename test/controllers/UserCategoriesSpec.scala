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
class UserCategoriesSpec extends Specification with JsonMatchers {

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

   val categories = Array("todo", "remember", "meeting")


   "Categorias de usuarios" should{

      "POST /:owner/categories debe crear la categoria al usuario y devolver su json y estado CREATED" in new WithApplication() {
         Category.exists(categories(0), userNicks(0)) must beFalse

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories").withFormUrlEncodedBody(("name", categories(0)))).get

         Category.exists(categories(0), userNicks(0)) must beTrue

         status(home) must equalTo(CREATED)
         contentType(home) must beSome.which(_ == "application/json")

         val jsonString = Json.stringify(contentAsJson(home))

         jsonString must /("name" -> categories(0))
      }

      "POST /:owner/categories debe devolver 404 si el usario no existe" in new WithApplication() {
         Category.exists(categories(0), noUserNick) must beFalse

         val home = route(FakeRequest(POST, "/" + noUserNick + "/categories").withFormUrlEncodedBody(("name", categories(0)))).get

         Category.exists(categories(0), noUserNick) must beFalse

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "POST /:owner/categories debe devolver bad request si no se pasa el nombre de la categoria" in new WithApplication() {
         Category.exists(categories(0), userNicks(0)) must beFalse

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories").withFormUrlEncodedBody(("nombre", categories(0)))).get

         Category.exists(categories(0), userNicks(0)) must beFalse

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")
      }

   }
}