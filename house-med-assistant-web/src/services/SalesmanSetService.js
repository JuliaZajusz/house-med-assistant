import {gql} from "apollo-boost";
import {client} from "./DefaultService";

export const setSalesmanSetName = (id, name) => {
    return client
    .mutate({
          mutation: gql`
              mutation setSalesmanSetName {
                  setSalesmanSetName(
                      id: "${id}",
                      name: "${name}",
                  )
              }
          `
      }
    );
}

export const getAllSalesmanSets = () => {
  console.log("getAllSalesmanSets")
    return client
    .query({
          query: gql`{
              salesmanSets {
                  id,
                  name,

              }
          }`,
      fetchPolicy: 'network-only'
      }
    );
}

export const findSalesmanSetById = (id) => {
  return client
  .query({
      query: gql`{
          findSalesmanSetById(id: "${id}") {
              id,
              name,
              places {
                  id,
                  firstName
                  lastName
                  address
                  coordinate {
                      id
                      location
                  }
                  tags {
                      name
                  }
              }
              paths {
                  places {
                      id,
                      firstName
                      lastName
                      address
                      coordinate {
                          id
                          location
                      }
                      tags {
                          name
                      }
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
                      name,
                      places {
                          id,
                          firstName
                          lastName
                          address
                          coordinate {
                              id
                              location
                          }
                          tags {
                              name
                          }
                      }
                      paths {
                          places {
                              id,
                              firstName
                              lastName
                              address
                              coordinate {
                                  id
                                  location
                              }
                              tags {
                                  name
                              }
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
    let places = set.places.map((place) => {
        return {
            ...place,
            tags: place.tags.map((tag) => {
                return tag.name
            })
        }
    });

    let json = JSON.stringify(places).replace(/\"([^(\")"]+)\":/g, "$1:");

    console.log("json", json);
    return client
    .mutate({
          mutation: gql`
              mutation newSalesmanSet {
                  newSalesmanSet(
                      patients: ${json}
                  ) {
                      id,
                      name,
                      places {
                          id,
                          firstName
                          lastName
                          address
                          coordinate {
                              id
                              location
                          }
                          tags {
                              name
                          }
                      }
                      paths {
                          id,
                          places {
                              id,
                              firstName
                              lastName
                              address
                              coordinate {
                                  id
                                  location
                              }
                              tags {
                                  name
                              }
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
    )
}

export const putSalesmanSet = (set) => {
    let places = set.places.map((place) => {
        return {
            ...place,
            tags: place.tags.map((tag) => {
                return tag.name
            })
        }
    })

    let paths = set.paths.map((path) => {
        return {
            ...path,
            places: path.places.map((place) => {
                return {
                    ...place,
                    tags: place.tags.map((tag) => {
                        return tag.name
                    })
                }
            })
        }
    })

    console.log("updateSalesmanSet", set.id)
    // console.log("updateSalesmanSet places: ", places)
    // console.log("updateSalesmanSet paths: ", paths)
    let mappedSet = {
        id: set.id,
        paths: paths,
        neighborhoodMatrix: set.neighborhoodMatrix,
        places: places
    }
    let json = JSON.stringify(mappedSet).replace(/\"([^(\")"]+)\":/g, "$1:");

    // console.log("updateSalesmanSet json", json)
    return client
    .mutate({
          mutation: gql`
              mutation updateSalesmanSet {
                  updateSalesmanSet(
                      salesmanSet: ${json}
                  ) {
                      id,
                      name,
                      places {
                          id,
                          firstName
                          lastName
                          address
                          coordinate {
                              id
                              location
                          }
                          tags {
                              name
                          }
                      }
                      paths {
                          id,
                          places {
                              id,
                              firstName
                              lastName
                              address
                              coordinate {
                                  id
                                  location
                              }
                              tags {
                                  name
                              }
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

