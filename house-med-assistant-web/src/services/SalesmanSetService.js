import {gql} from "apollo-boost";
import {client} from "./DefaultService";

// export const getSalesmanSet = (id) => {
//     return null;
// }

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


export const putSalesmanSet = (set) => {
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
