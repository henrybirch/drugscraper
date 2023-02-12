resource "google_artifact_registry_repository" "artifact_repo" {
  location      = var.default_location
  repository_id = var.artifact_repository_id
  description   = "Repo for drugscraper api and get-all images"
  format        = "DOCKER"
}