package model

/**
  * Created by anurag on 3/1/17.
  */
object SortingType extends Enumeration{
  type SortingType = Value
  val PRICEASC, PRICEDESC = Value

  def isValidType(s: String) = values.exists(_.toString == s)
}
