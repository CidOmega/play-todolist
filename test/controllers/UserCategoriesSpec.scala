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

   def mustBeJsArrayAndHasLength(json:JsValue, expected: Int): Unit =
   {
      json match {
         case arr: JsArray => arr.value.length === expected
         case _ => throw new Exception("JsValue must be an array but it isn't")
      }
   }


   "Categorias de usuarios" should{

      "GET /:owner/categories debe debolver OK y le josn del array de categorias dle user" in new WithApplication() {
         Category.create(categories(0), userNicks(0))
         Category.create(categories(1), userNicks(0))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/categories")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         mustBeJsArrayAndHasLength(contentAsJson(home), 2)
      }

      "GET /:owner/categories debe debolver 404 si le user no existe" in new WithApplication() {
         val home = route(FakeRequest(GET, "/" + noUserNick + "/categories")).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "POST /:owner/categories debe crear la categoria al usuario y devolver su json y estado CREATED" in new WithApplication() {
         Category.exists(categories(0), userNicks(0)) must beFalse

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories").withFormUrlEncodedBody(("name", categories(0)))).get

         status(home) must equalTo(CREATED)
         contentType(home) must beSome.which(_ == "application/json")

         Category.exists(categories(0), userNicks(0)) must beTrue

         val jsonString = Json.stringify(contentAsJson(home))

         jsonString must /("name" -> categories(0))
      }

      "POST /:owner/categories debe devolver 404 si el usario no existe" in new WithApplication() {
         Category.exists(categories(0), noUserNick) must beFalse

         val home = route(FakeRequest(POST, "/" + noUserNick + "/categories").withFormUrlEncodedBody(("name", categories(0)))).get

         status(home) must equalTo(NOT_FOUND)
         contentType(home) must beSome.which(_ == "text/plain")

         Category.exists(categories(0), noUserNick) must beFalse
      }

      "POST /:owner/categories debe devolver bad request si no se pasa el nombre de la categoria" in new WithApplication() {
         Category.exists(categories(0), userNicks(0)) must beFalse

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories").withFormUrlEncodedBody(("nombre", categories(0)))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")

         Category.exists(categories(0), userNicks(0)) must beFalse
      }

   }

   "Categoria (concreta)" should{

      "GET /:owner/categories/:category debe devolver la categoria en json" in new WithApplication() {
         Category.create(categories(0), userNicks(0))
         Category.create(categories(1), userNicks(0))

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/categories/" + categories(0))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         Json.stringify(contentAsJson(home)) must /("name" -> categories(0))
      }

      "GET /:owner/categories/:category/tasks debe devolver las tareas pertenecientes a la categoria en json" in new WithApplication() {
         Category.create(categories(0), userNicks(0))
         Category.create(categories(1), userNicks(0))

         for(i <- 0 to 1)
         {
            var id = Task.create(labels(i), userNicks(0), None).get
            Category.addTaskToCategory(id, categories(0), userNicks(0))
         }

         val home = route(FakeRequest(GET, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks")).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         mustBeJsArrayAndHasLength(contentAsJson(home), 2)
      }

      "POST /:owner/categories/:category/tasks debe devolver la tarea en json (habiendo sido añadida a la categoría)" in new WithApplication() {
         Category.create(categories(0), userNicks(0))

         var id = Task.create(labels(0), userNicks(0), None).get

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", id.toString))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         Task.tasksFromUserCategory(userNicks(0), categories(0)).length === 1

         Json.stringify(contentAsJson(home)) must /("categories") /#(0) /("name" -> categories(0))
      }

      "POST /:owner/categories/:category/tasks debe devolver bad request si la tarea y la categoria no son del mismo usuario" in new WithApplication() {
         Category.create(categories(0), userNicks(0))

         var id = Task.create(labels(0), userNicks(1), None).get

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", id.toString))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")

         Task.tasksFromUserCategory(userNicks(0), categories(0)).length === 0
      }

      "POST /:owner/categories/:category/tasks debe devolver bad request si la tarea ya pertenece a la categoria" in new WithApplication() {
         Category.create(categories(0), userNicks(0))
         var id = Task.create(labels(0), userNicks(1), None).get
         Category.addTaskToCategory(id, categories(0), userNicks(0))

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", id.toString))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")

         Task.tasksFromUserCategory(userNicks(0), categories(0)).length === 1
      }

      "POST /:owner/categories/:category/tasks debe devolver bad request si la tarea no existe" in new WithApplication() {
         Category.create(categories(0), userNicks(0))

         val home = route(FakeRequest(POST, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", (-1L).toString))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")

         Task.tasksFromUserCategory(userNicks(0), categories(0)).length === 0
      }

      "DELETE /:owner/categories/:category/tasks debe devolver la tarea en json (habiendo sido eliminada de la categoría)" in new WithApplication() {
         Category.create(categories(0), userNicks(0))
         var id = Task.create(labels(0), userNicks(0), None).get
         Category.addTaskToCategory(id, categories(0), userNicks(0))

         val home = route(FakeRequest(DELETE, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", id.toString))).get

         status(home) must equalTo(OK)
         contentType(home) must beSome.which(_ == "application/json")

         Task.tasksFromUserCategory(userNicks(0), categories(0)).length === 0

         Json.stringify(contentAsJson(home)) must not */("name" -> categories(0))
      }

      "DELETE /:owner/categories/:category/tasks debe devolver bad request si la tarea y la categoria no son del mismo usuario" in new WithApplication() {
         Category.create(categories(0), userNicks(0))
         var id = Task.create(labels(0), userNicks(1), None).get

         val home = route(FakeRequest(DELETE, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", id.toString))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "DELETE /:owner/categories/:category/tasks debe devolver bad request si la tarea no pertenecía a la categoria" in new WithApplication() {
         Category.create(categories(0), userNicks(0))
         var id = Task.create(labels(0), userNicks(1), None).get

         val home = route(FakeRequest(DELETE, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", id.toString))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")
      }

      "DELETE /:owner/categories/:category/tasks debe devolver bad request si la tarea no existe" in new WithApplication() {
         Category.create(categories(0), userNicks(0))

         val home = route(FakeRequest(DELETE, "/" + userNicks(0) + "/categories/" + categories(0) + "/tasks").
            withFormUrlEncodedBody(("id", (-1L).toString))).get

         status(home) must equalTo(BAD_REQUEST)
         contentType(home) must beSome.which(_ == "text/plain")
      }

   }
}