variable "gcp_project" {
  type = string
  default = "drugscraper-377523"
}

variable "sa_id" {
  type = string
  default = "drugscraper-sa-5"
}

variable "workload_identity_pool_id" {
  type = string
  default = "cicd-pool-10"
}

variable "artifact_repository_id" {
  type = string
  default = "artifact-repo-1"
}

variable "default_location" {
  type = string
  default = "us-central1"
}

variable "github_token" {
  type = string
  sensitive = true

}


