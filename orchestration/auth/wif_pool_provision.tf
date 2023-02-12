resource google_project_service "sa_key_api" {
  project = var.gcp_project
  service = "iamcredentials.googleapis.com"
}

resource "google_project_iam_member" "workload_identity_pool_member" {
  project = var.gcp_project
  role    = "roles/iam.workloadIdentityUser"
  member  = "serviceAccount:${google_service_account.sa.email}"
}

resource "google_project_iam_member" "bq_member" {
  member  = "serviceAccount:${google_service_account.sa.email}"
  project = var.gcp_project
  role    = "roles/bigquery.dataEditor"
}

resource "google_iam_workload_identity_pool" "cicd_pool" {
  workload_identity_pool_id = "cicd-pool"             # Name of the pool for use in API calls
  display_name              = "CI/CD Identity Pool"   # Name of the pool in the console
}

resource "google_iam_workload_identity_pool_provider" "github" {
  workload_identity_pool_id          = google_iam_workload_identity_pool.cicd_pool.workload_identity_pool_id
  workload_identity_pool_provider_id = "github"
  display_name                       = "GitHub CI/CD Pool Provider"

  attribute_mapping = {
    "google.subject"       = "assertion.sub", # Make the Google Subject the GitHub Identity
    "attribute.actor"      = "assertion.actor", # Map the Actor (GitHub User) the Google Actor
    "attribute.aud"        = "assertion.aud", # Map the audience
    "attribute.repository" = "assertion.repository" # Custom attribute to see what repository is used
  }
  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }
}



