package domain

import com.eaio.uuid._

case class Id[T](value: String) extends AnyVal

case object Id {
  def create[T](): Id[T] = Id[T](new UUID().toString.take(8))
}
