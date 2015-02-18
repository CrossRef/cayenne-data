# cayenne-data

Serves `data.crossref.org` and `id.crossref.org` by proxying requests to
the cayenne API at `api.crossref.org`.

## Docker

Build an uberjar:

    lein uberjar

Build a docker image (copies in the uberjar):

    docker build -t cayenne-data .

If the project version number in project.clj changes, the Dockerfile
must also be updated to include the JAR with correct version number.
