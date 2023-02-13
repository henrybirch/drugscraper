resource "google_service_account_iam_member" "wif_sa" {
  service_account_id = "projects/${var.gcp_project}/serviceAccounts/${google_service_account.sa.email}"
  role               = "roles/iam.workloadIdentityUser"
  member             = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.cicd_pool
  .name}/attribute.repository/henrybirch/drugscraper"
}

locals {
  sa_member = "serviceAccount:${google_service_account.sa.email}"
}

resource "google_project_iam_member" "gcs_admin" {
  member  = "serviceAccount:${google_service_account.sa.email}"
  role    = "roles/storage.admin"
  project = var.gcp_project
}


resource "google_project_iam_member" "artifact_repository" {
  role               = "roles/artifactregistry.admin"
  member             = local.sa_member
  project = var.gcp_project
}

resource "google_project_iam_member" "act_as" {
  role = "roles/serviceAccountUser"
  member = local.sa_member
  project = var.gcp_project
}

resource "google_project_iam_member" "act_as" {
  role = "roles/run.admin"
  member = local.sa_member
  project = var.gcp_project
}

