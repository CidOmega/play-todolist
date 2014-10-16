import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models.User


@RunWith(classOf[JUnitRunner])
class UserSpec extends Specification {

   "User" should {

      "Devolver null cuando busquemos un nick que no existe" in new WithApplication{
         User.read("no existo") must beNull
      }

      "Devolver el usuario cuando busquemos un nick existente con los datos pertinentes" in new WithApplication{
         val usuario = User.read("edgar")

         usuario must not be null
         usuario.nick must beEqualTo("edgar")
      }
   }
}
