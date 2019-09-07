import ApolloClient, {gql} from 'apollo-boost';

export const client = new ApolloClient({
    // uri: 'https://48p1r2roz4.sse.codesandbox.io',
    uri: 'http://localhost:9000/graphql',
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
