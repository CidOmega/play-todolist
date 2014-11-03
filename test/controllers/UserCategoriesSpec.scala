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

      "POST /:owner/categories debe crear la categoria al usuario y devolver su json" in new WithApplication() {
         Category.exists(categories(0), userNicks(0)) must beFalse

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories").withFormUrlEncodedBody(("name", categories(0)))).get

         Category.exists(categories(0), userNicks(0)) must beTrue

         status(home) must equalTo(CREATED)
         contentType(home) must beSome.which(_ == "application/json")

         val jsonString = Json.stringify(contentAsJson(home))

         jsonString must /("name" -> categories(0))
      }

   }
}