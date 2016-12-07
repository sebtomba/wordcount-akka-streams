package object domain {

  type Tagged[U] = {type Tag = U}

  type @@[T, U] = T with Tagged[U]

}
