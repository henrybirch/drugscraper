terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "4.52.0"
    }
    github = {
      source  = "integrations/github"
      version = "5.17.0"
    }
  }
}

provider "google" {
  project = var.gcp_project
  region  = "us-central-1"
}

provider "github" {
  owner = var.github_owner
}

resource "google_artifact_registry_repository" "artifact_repo" {
  location      = var.default_location
  repository_id = var.artifact_repository
  description   = "Repo for drugscraper api and get-all images"
  format        = "DOCKER"
}








