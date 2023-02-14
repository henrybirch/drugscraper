resource google_project_service "compute_engine" {
  project = var.gcp_project
  service = "compute.googleapis.com"
}