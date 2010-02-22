package models
 
import java.util._
import javax.persistence._
 
import play.db.jpa._
import play.data.validation._


@Entity
class Car private (

    @Required
        var name:String

) extends Model
 

