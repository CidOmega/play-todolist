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

      val names = Array("todo", "remember", "meeting")

      "Crear correctamente una categoría asociada a un usuario" in new WithApplication() {
         Category.create(names(0), user.nick) === 1
      }

   }

}
