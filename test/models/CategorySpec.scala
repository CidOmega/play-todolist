import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.{Json, JsValue}

import models._

@RunWith(classOf[JUnitRunner])
class CategorySpecSpec extends Specification with JsonMatchers {

   "Categoria" should{

      val user = User("edgar")
      val noUser = User("no existe")

      val names = Array("todo", "remember", "meeting")

      "Crear correctamente una categoría asociada a un usuario" in new WithApplication() {
         Category.create(names(0), user.nick) === 1
      }

      "Leer correctamente una categoría asociada a un usuario" in new WithApplication() {
         Category.create(names(0), user.nick)

         val cat = Category.readOption(names(0), user.nick)

         cat must beSome(Category(names(0), user))
      }

      "Leer None si no existe la categoría o el user" in new WithApplication() {
         Category.create(names(0), user.nick)

         val cat = Category.readOption(names(0), noUser.nick)

         cat must beNone
      }

      "Devolver correctamente la existencia de una categoría" in new WithApplication() {
         Category.exists(names(0), user.nick) === false

         Category.create(names(0), user.nick)

         Category.exists(names(0), user.nick) === true
      }

      "Asociar correctamente la tarea a una categoría" in new WithApplication() {
         Category.create(names(0), user.nick)

         var id = Task.create("test", user.nick, None).get
         Category.addTaskToCategory(id, names(0), user.nick) === 1
      }

      "Devolver las categorias de una tarea" in new WithApplication() {
         for(i <- 0 to 2)
            Category.create(names(i), user.nick)

         var id = Task.create("test", user.nick, None).get

         for(i <- 0 to 2)
            Category.addTaskToCategory(id, names(i), user.nick)

         Category.categoriesFromTask(id).length === 3
      }

      "Borrar las categorias de una tarea" in new WithApplication() {
         for(i <- 0 to 2)
            Category.create(names(i), user.nick)

         var id = Task.create("test", user.nick, None).get

         for(i <- 0 to 2)
            Category.addTaskToCategory(id, names(i), user.nick)

         Category.categoriesFromTask(id).length === 3

         Category.deleteTaskToCategory(id, names(1), user.nick)

         Category.categoriesFromTask(id).length === 2
      }

      "Generar correctamente el json" in new WithApplication() {
         var jsonSring = Json.stringify(Json.toJson(Category(names(0), user)))

         jsonSring must /("name" -> names(0))
         jsonSring must not */("owner")//No queremos los datos del usuario
         jsonSring must not */("user")//No queremos los datos del usuario
         jsonSring must not */("nick")//No queremos los datos del usuario
      }

   }

}
