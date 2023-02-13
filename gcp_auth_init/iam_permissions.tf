resource "google_project_iam_member" "gcs_admin" {
  member  = "serviceAccount:${google_service_account.sa.email}"
  project = var.gcp_project
  role    = "roles/storage.admin"
}

locals {
  member = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.cicd_pool
  .name}/attribute.repository/henrybirch/drugscraper"
  service_account_id = "projects/${var.gcp_project}/serviceAccounts/${google_service_account.sa.email}"
}

resource "google_service_account_iam_member" "wif_sa" {
  service_account_id = local.service_account_id
  role               = "roles/iam.workloadIdentityUser"
  member             = local.member
}

resource "google_service_account_iam_member" "artifact_repository" {
  role   = "roles/artifactregistry.admin"
  member = local.member
  service_account_id = local.service_account_id
}