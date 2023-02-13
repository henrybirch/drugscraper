variable "gcp_project" {
  type    = string
  default = "drugscraper-377523"
}

variable "github_token" {
  type      = string
  sensitive = true
}

variable "default_location" {
  type    = string
  default = "us-central1"
}

variable "artifact_repository_id" {
  type    = string
  default = "drugscraper-repo-2"
}

variable "drugscraper_api_image_name" {
  type    = string
  default = "drugscraper_api"
}

variable "drugscraper_api_image_tag" {
  type    = string
  default = "latest"
}


