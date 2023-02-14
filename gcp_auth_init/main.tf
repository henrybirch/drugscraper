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

provider google {
  project = var.gcp_project
}

provider github {
  owner = "henrybirch"
  token = var.github_token
}
