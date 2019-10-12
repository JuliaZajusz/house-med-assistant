import {gql} from "apollo-boost";
import {client} from "./DefaultService";

// export const getSalesmanSet = (id) => {
//     return null;
// }

export const getAllSalesmanSets = () => {
    return client
    .query({
          query: gql`{
              salesmanSets {
                  id,
                  places {
                      id,
                      location
                  }
                  paths {
                      places {
                          id,
                          location
                      },
                      value
                  }
                  neighborhoodMatrix {
                      startCoordinateId,
                      endCoordinateId,
                      value
                  }
              }
          }`
      }
    );
}

export const upgradeSalesmanSet = (id,
                                   timeInSec,
                                   populationSize,
                                   parentPopulationSize) => {

    console.log(id,
        timeInSec,
        populationSize,
        parentPopulationSize)
    return client
    .mutate({
            mutation: gql`
                mutation upgradeSalesmanSet {
                    upgradeSalesmanSet(
                        id: "${id}",
                        timeInSec: ${timeInSec},
                        populationSize: ${populationSize},
                        parentPopulationSize: ${parentPopulationSize}
                    ) {
                        id,
                        places {
                            id,
                            location
                        }
                        paths {
                            places {
                                id,
                                location
                            },
                            value
                        }
                        neighborhoodMatrix {
                            startCoordinateId,
                            endCoordinateId,
                            value
                        }
                    }
                }
            `
        }
    );
}


export const postSalesmanSet = (set) => {
    if (set.id != null) {
        return putSalesmanSet(set)
    }
    ;
    let places = set.places.map((place) => {
        return {location: place.location}
    })

    let json = JSON.stringify(places).replace(/\"([^(\")"]+)\":/g, "$1:");

    return client
    .mutate({
            mutation: gql`
                mutation newSalesmanSet {
                    newSalesmanSet(
                        coordinates: ${json}
                    ) {
                        id,
                        places {
                            id,
                            location
                        }
                        paths {
                            id,
                            places {
                                id,
                                location
                            },
                            value
                        }
                        neighborhoodMatrix {
                            startCoordinateId,
                            endCoordinateId,
                            value
                        }
                    }
                }
            `
        }
    );
}

export const putSalesmanSet = (set) => {
    let places = set.places.map((place) => {
        return {
            id: place.id,
            location: place.location
        }
    })
    let mappedSet = {
        id: set.id,
        paths: set.paths,
        neighborhoodMatrix: set.neighborhoodMatrix,
        places: places
    }
    let json = JSON.stringify(mappedSet).replace(/\"([^(\")"]+)\":/g, "$1:");

    return client
    .mutate({
            mutation: gql`
                mutation updateSalesmanSet {
                    updateSalesmanSet(
                        salesmanSet: ${json}
                    ) {
                        id,
                        places {
                            id,
                            location
                        }
                        paths {
                            id,
                            places {
                                id,
                                location
                            },
                            value
                        }
                        neighborhoodMatrix {
                            startCoordinateId,
                            endCoordinateId,
                            value
                        }
                    }
                }
            `
        }
    );
}


export const deleteSalesmanSet = (id) => {
    return client
    .mutate({
          mutation: gql`
              mutation deleteSalesmanSet {
                  deleteSalesmanSet(
                      id: "${id}"
                  )
              }
          `
      }
    );
}

