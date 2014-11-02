package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.json._

case class Category(name: String, owner: User)

object Category
{

   def create(name: String, owner: String) : Int = DB.withConnection
   {
      implicit c => SQL("insert into category (name, owner) values ({name}, {owner})").
         on('name -> name, 'owner -> owner).executeUpdate()
   }

}