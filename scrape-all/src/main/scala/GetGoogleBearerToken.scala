import com.google.auth.oauth2.{
  GoogleCredentials,
  IdTokenCredentials,
  IdTokenProvider
}

object GetGoogleBearerToken {

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
      .setTargetAudience("https://drugscraper-api-7x2jgthdga-uc.a.run.app")
      .build()
    idBuilder.refresh()
    idBuilder.getIdToken.getTokenValue
  }
}
