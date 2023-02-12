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

module "auth" {
  source = "./auth"
}

module "artifact_repository" {
  source = "./artifact_repository"
}








