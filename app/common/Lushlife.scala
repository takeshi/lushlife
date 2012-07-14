package common

object Lushlife {

  lazy val isCloud: Boolean = {
    System.getProperty("cloud.provider.url") != null
  }

}