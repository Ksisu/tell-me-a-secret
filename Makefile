compose=docker-compose -f docker-compose.dev.yml -p tell-me-a-secret

default: help

up: ## Spin up services
	$(compose) up -d

stop: ## Stop services
	$(compose) stop

down: ## Destroy all services and volumes
	$(compose) down -v

redis: ## Open redis console
	$(compose) exec db bash -c "redis-cli"

help: ## This help message
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' -e 's/:.*#/: #/' | column -t -s '##'
