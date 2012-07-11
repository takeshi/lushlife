package common
import model.ErrorMessage

trait Validator[T] {
  def validate(t: T): List[ErrorMessage]

  def notEmpty(value: String, property: String, label: String)(f: ErrorMessage => Unit) {
    if (value == null || value.isEmpty()) {
      f(ErrorMessage(property, label + "は必須項目です。"))
    }
  }
}

object Validator {
  def validate[T](t: T)(implicit validator: Validator[T]): List[ErrorMessage] = {
    validator.validate(t)
  }
}