# cayenne-data

Serves `data.crossref.org` and `id.crossref.org` by proxying requests to
the cayenne API at `api.crossref.org`.

## Docker / Convox
	
### Run for local development

Ensure you have created and are running a docker machine:

    docker-machine create --driver virtualbox default
	docker-machine start default
	eval "$(docker-machine env default)"
	
Run on local machine:

    convox start
	
### Deploy to AWS

    convox login grid.convox.com --password <YOUR_CONVOX_GRID_PASSWORD>
	convox apps create cayenne-data
	convox deploy
