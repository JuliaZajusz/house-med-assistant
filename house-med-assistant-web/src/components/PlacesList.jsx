import React from 'react';
import {useQuery} from '@apollo/react-hooks';
import {gql} from 'apollo-boost';

export default function PlacesList() {
    const {loading, error, data} = useQuery(gql`
        {places {
            id,
            name,
            coordinate {
                id,
                location
            }
        }
        }
    `);

    if (loading) return null;
    if (error) return null;
    return data
}
