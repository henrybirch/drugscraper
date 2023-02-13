resource "google_project_iam_member" "gcs_admin" {
  member  = "serviceAccount:${google_service_account.sa.email}"
  project = var.gcp_project
  role    = "roles/storage.admin"
}

resource "google_service_account_iam_member" "wif_sa" {
  service_account_id = "projects/${var.gcp_project}/serviceAccounts/${google_service_account.sa.email}"
  role               = "roles/iam.workloadIdentityUser"
  member             = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.cicd_pool.name}/attribute.repository/${var.drugscraper_repository}"
}