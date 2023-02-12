variable "drugscraper_api_repository" {
  type    = string
  default = "henrybirch/drugscraper"
}

variable "drugscraper_api_repository_name" {
  type    = string
  default = "drugscraper"
}

variable "drugscraper_get_all_repository" {
  type    = string
  default = "drugscraper-get-all"
}

variable "gcp_project" {
  type    = string
  default = "drugscraper-377523"
}

variable "github_owner" {
  type    = string
  default = "henrybirch"
}

variable "default_location" {
  type    = string
  default = "us-central1"
}

variable "artifact_repository" {
  type    = string
  default = "drugscraper-repo"
}

variable "drugscraper_api_image_name" {
  type    = string
  default = "drugscraper_api"
}

variable "drugscraper_api_image_tag" {
  type    = string
  default = "latest"
}
