package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.json._

case class User(nick: String)

object User
{
   /**
    * Devuelve un Option[User] del nick dado
    * @param nick nick del user a recuperar
    * @return Some(User) con el user existente OR None si el user no existe
    */
   def readOption(nick: String): Option[User] = DB.withConnection
   {
      implicit c => SQL("select * from people where nick = {nick}").on('nick -> nick).as(user singleOpt)
   }

   /**
    * Devuelve un Option[User] del nick dado
    * @param nick nick del user a recuperar
    * @return Some(User) con el user existente OR None si el user no existe
    */
   def read(nick: String): User =
   {
      readOption(nick) match
      {
         case Some(user) => user
         case None => null
      }
   }


   /**
    * @param nick del user cuya existencia se quiere comprovar
    * @return si el user existe
    */
   def Exists(nick: String): Boolean =
   {
      readOption(nick) match
      {
         case Some(_) => true
         case None => false
      }
   }


   /**
    * Conversor SQL a User
    */
   val user =
   {
      get[String]("nick") map
      {
         case nick => User(nick)
      }
   }


   /**
    * Conversor User a Json
    */
   implicit val userWrites = new Writes[User]
   {
      def writes(user: User) = Json.obj(
         "nick" -> user.nick
      )
   }
}