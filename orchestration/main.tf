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
    http = {
      source  = "hashicorp/http"
      version = "3.2.1"
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

module "artifact_repository" {
  source                 = "./artifact_repository"
  artifact_repository_id = "drugscraper-repository"
  default_location       = var.default_location
}

module "scrape_api_cloud_run" {
  depends_on       = [module.artifact_repository]
  source           = "./scrape_api_cloud_run"
  api_name         = "drugscraper-api"
  default_location = var.default_location
  image_name       = var.drugscraper_api_image_name
  image_tag        = var.drugscraper_api_image_tag
}

module "inject_image_info_github" {
  source                      = "./inject_image_info_github"
  api_image_tag               = var.drugscraper_api_image_tag
  api_image_name              = var.drugscraper_api_image_name
  drugscraper_repository_name = var.drugscraper_repository_name
  artifact_repository_url     = join("/", ["gcr.io", var.gcp_project, var.artifact_repository])
}







