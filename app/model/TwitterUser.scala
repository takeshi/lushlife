package model
import twitter4j.auth.AccessToken

case class TwitterUser(name: String, accessToken: String, accessTokenSecret: String) {
  def this(accessToken: AccessToken) = this(accessToken.getScreenName(), accessToken.getToken(), accessToken.getTokenSecret())
}
