import com.google.auth.oauth2.{
  GoogleCredentials,
  IdTokenCredentials,
  IdTokenProvider
}

object GetGoogleBearerToken {
  val drugscrapeApiUrl = sys.env("DRUGSCRAPER_API_URL")

  /** Returns google bearer token based on application's default google credentials
    * @return
    */
  def getGoogleBearerToken(): String = {
    val credentials: GoogleCredentials = GoogleCredentials
      .getApplicationDefault()
      .createScoped("https://www.googleapis.com/auth/cloud-platform")
    val idBuilder: IdTokenCredentials = IdTokenCredentials
      .newBuilder()
      .setIdTokenProvider(credentials.asInstanceOf[IdTokenProvider])
      .setTargetAudience(drugscrapeApiUrl)
      .build()
    idBuilder.refresh()
    idBuilder.getIdToken.getTokenValue
  }
}
