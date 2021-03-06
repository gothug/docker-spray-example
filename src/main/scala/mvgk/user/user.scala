package mvgk.user

/**
 * Created by kojuhovskiy on 16/02/15.
 */

case class UserAccount(email: String, name: Option[String], var subscribed: Boolean)

case class User(accounts: Seq[UserAccount]) {
  def updateAccounts(account: UserAccount) = {
    for (a <- accounts) {
      if (a.email == account.email) {
        a.subscribed = account.subscribed
      }
    }
  }
}

object User {
  private val accounts =
    Seq(
      UserAccount("kojuhovskiy@gmail.com", Some("Vasek"), subscribed = true),
      UserAccount("Olga.Goi@gmail.com", Some("Olga Goi"), subscribed = false)
    )

  val user = User(accounts)
}
