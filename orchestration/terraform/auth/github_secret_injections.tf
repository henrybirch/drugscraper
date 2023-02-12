resource "github_actions_secret" "wif_pool" {
  repository      = var.drugscraper_repository_name
  secret_name     = "WIF_POOL"
  plaintext_value = google_iam_workload_identity_pool_provider.github.name
}

resource "github_actions_secret" "wif_sa_email" {
  repository      = var.drugscraper_repository_name
  secret_name     = "SA_EMAIL"
  plaintext_value = google_service_account.sa.email
}

