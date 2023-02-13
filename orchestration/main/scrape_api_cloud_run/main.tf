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

data "google_container_registry_image" "api_image" {
  name = var.image_name
  tag  = var.image_tag
}

resource "google_project_service" "run_api" {
  service            = "run.googleapis.com"
  disable_on_destroy = true
}

resource "google_cloud_run_service" "api" {
  name     = var.api_name
  location = var.default_location

  template {
    spec {
      containers {
        image = data.google_container_registry_image.api_image.image_url
      }
    }

  }
}
