package engine.domain

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import julienrf.variants.Variants

import scala.util.{Success, Failure, Try}

package object json{

  implicit val channelId= Json.format[ChannelId]
  implicit val webChannel = Json.format[WebChannel]
  implicit val identityChannel = Json.format[IdentityChannel]
  implicit val phoneChannel = Json.format[PhoneChannel]
  implicit val smsChannel = Json.format[SmsChannel]
  implicit val channel: Format[Channel] = Variants.format[Channel]("type")
  implicit val userId= Json.format[UserId]
  implicit val groupId = Json.format[GroupId]
  implicit val contact = Json.format[Contact]
  implicit val group = Json.format[Group]

  implicit val user = Json.format[User]
    implicit val userSearchReads: Reads[SearchUser] =  (
        (__ \ "id").read[UserId] and
        (__ \ "first").read[String] and
        (__ \ "last").read[String]
        )(SearchUser.apply _)
    implicit val userSearchWrites = Json.writes[SearchUser]
    implicit val userSeqReads: Reads[Seq[SearchUser]] = Reads[Seq[SearchUser]](js => {
        JsSuccess {
            val arr = translateFirebaseJson(js)
            var ls = Seq[SearchUser]()
            var i = 0
            var go = true
            while(go) {
                Try {
                    val x = arr(i)
                    println(x)
                    arr(i).as[SearchUser]
                } match {
                    case Success(r) =>
                        ls = ls :+ r
                        i += 1
                    case Failure(e) => go = false
                }
            }
            ls
        }
    })

    def translateFirebaseJson(s: JsValue): JsArray = {
        val js = Json.stringify(s).foldLeft((List[Char](), 0))((acc, c) => {
            acc match {
                case (str, 0) => c match {
                    case ParsingTokens.Open => (str, acc._2 + 1)
                    case ParsingTokens.Close => (str, acc._2 -1)
                    case _ => acc
                }
                case (str, 1) => c match {
                    case ParsingTokens.Open => (c :: str, acc._2 + 1)
                    case ParsingTokens.Close => (str, acc._2 -1)
                    case _ => acc
                }
                case (str, 2) => c match {
                    case ParsingTokens.Close => (c :: str, acc._2 -1)
                    case ParsingTokens.Open => (c ::str, acc._2 +1)
                    case _ =>  (c :: str, 2)
                }
                case (str, x) => c match {
                    case ParsingTokens.Close => (c :: str, x -1)
                    case ParsingTokens.Open => (c :: str, x + 1)
                    case _ => (c :: str, x)
                }
            }
        })._1.reverse.mkString("[","", "]").replace("}{", "},{")

        Json.arr(Json.parse(js))
    }

    private object ParsingTokens {
        val Open = '{'
        val Close = '}'
        val FieldName = ':'
        val Quote = '"'
        val Comma = ','
    }
}

