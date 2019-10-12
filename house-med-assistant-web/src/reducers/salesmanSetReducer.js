export default (state = {
  mapSalesmanSet: null,
}, action) => {
  switch (action.type) {
    case 'SET_SALESMAN_SET':
      return {
        ...state,
        mapSalesmanSet: action.payload
      };
    case 'ADD_COORDINATE_TO_SALESMAN_SET':
      return {
        ...state,
        mapSalesmanSet: state.mapSalesmanSet
          ? {...state.mapSalesmanSet, places: [...state.mapSalesmanSet.places, action.payload]}
          : {places: [action.payload]}
      };
    default:
      return state
  }
}
