import ApolloClient, {gql} from 'apollo-boost';
import {InMemoryCache} from "apollo-cache-inmemory";

export const client = new ApolloClient({
    uri: 'http://localhost:9000/graphql',
    cache: new InMemoryCache({
        addTypename: false
    })
});


export const loadSalesmanSets = () => {
    return client
    .query({
        query: gql`
            {
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
            }
        `
    })
// .then(result => console.log(result));
}

export const addCoordinate = (coordinate) => {
    let lat = coordinate[0]
    let lng = coordinate[1]

    return client
    .mutate({
            mutation: gql`
                mutation nowyKoordynat {
                    newCoordinate(
                        location: [${lat}, ${lng}]
                    ){
                        id, location
                    }
                }
            `
        }
    );
}



