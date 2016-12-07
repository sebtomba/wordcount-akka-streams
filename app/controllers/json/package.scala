package controllers

import domain.Id
import play.api.libs.json.{JsString, Writes}

package object json {

  def idWrites[E] = new Writes[Id[E]] {
    def writes(id: Id[E]) =
      JsString(id.value)
  }

}
