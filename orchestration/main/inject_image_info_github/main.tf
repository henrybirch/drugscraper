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

resource "github_actions_secret" "api_image_name" {
  repository      = "drugscraper"
  secret_name     = "API_IMAGE_NAME"
  plaintext_value = var.api_image_name
}

resource "github_actions_secret" "api_tag_name" {
  repository      = "drugscraper"
  secret_name     = "API_IMAGE_TAG"
  plaintext_value = var.api_image_tag
}

resource "github_actions_secret" "artifact_repository_url" {
  repository      = "drugscraper"
  secret_name     = "ARTIFACT_REPOSITORY_URL"
  plaintext_value = var.artifact_repository_url
}

