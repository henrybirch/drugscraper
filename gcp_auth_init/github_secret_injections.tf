resource "github_actions_secret" "wif_pool" {
  repository      = "drugscraper"
  secret_name     = "WIF_POOL"
  plaintext_value = google_iam_workload_identity_pool_provider.github.name
}

resource "github_actions_secret" "wif_sa_email" {
  repository      = "drugscraper"
  secret_name     = "SA_EMAIL"
  plaintext_value = google_service_account.sa.email
}

resource "github_actions_secret" "artifact_repository_id" {
  repository = "drugscraper"
  secret_name = "ARTIFACT_REPOSITORY_ID"
  plaintext_value = var.artifact_repository_id
}

resource "github_actions_secret" "default_location" {
  repository = "drugscraper"
  secret_name = "DEFAULT_LOCATION"
  plaintext_value = var.default_location
}

