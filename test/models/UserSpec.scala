import org.specs2.mutable._
import org.specs2.matcher._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

import models.User


@RunWith(classOf[JUnitRunner])
class UserSpec extends Specification with JsonMatchers{

   "User" should {

      "Devolver null cuando busquemos un nick que no existe (read)" in new WithApplication{
         User.read("no existo") must beNull
      }

      "Devolver el usuario cuando busquemos un nick existente con los datos pertinentes (read)" in new WithApplication{
         val usuario = User.read("edgar")

         usuario must not be null
         usuario.nick must beEqualTo("edgar")
      }

      "Devolver correctamente la existencia de los usuarios" in new WithApplication() {
         User.Exists("Nadie") must beFalse

         User.Exists("edgar") must beTrue
         User.Exists("Edgar") must beFalse
         User.Exists("EDGAR") must beFalse

         User.Exists("domingo") must beTrue
         User.Exists("Domingo") must beFalse
         User.Exists("DOMINGO") must beFalse
      }

      "Devolver correctamente el option de los usuarios solicitados (readOption)" in new WithApplication() {
         User.readOption("Nadie") must beNone
         User.readOption("EDGAR") must beNone
         User.readOption("Edgar") must beNone
         User.readOption("DOMINGO") must beNone
         User.readOption("Domingo") must beNone

         var userOpt = User.readOption("edgar")
         userOpt must beSome(User("edgar"))
         userOpt.get.nick mustEqual "edgar"

         userOpt = User.readOption("domingo")
         userOpt must beSome(User("domingo"))
         userOpt.get.nick mustEqual "domingo"
      }

      "Generar el Json con el nick" in new WithApplication() {
         val json = Json.stringify(Json.toJson(User("Nadie")))

         json must  /("nick" -> "Nadie")
      }
   }
}
