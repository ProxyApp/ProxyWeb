package controllers.translators

import play.api.libs.json.JsValue

trait JsonTranslator[C] {
 def translate(json: JsValue): Option[C]
}
