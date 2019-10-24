export default (state = {
  mapSalesmanSet: null,
  salesmanSets: []
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
    case 'GET_SALESMAN_SETS':
      return {
        ...state,
        salesmanSets: action.payload
      };
    case 'REMOVE_SALESMAN_SET':
      return {
        ...state,
        salesmanSets: state.salesmanSets.filter(function (value, index, arr) {
          console.log(!action.payload === value.id, action.payload, value.id)
          return !action.payload === value.id;
        })
      };
    default:
      return state
  }
}
