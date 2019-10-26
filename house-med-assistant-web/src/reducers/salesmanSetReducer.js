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
    case 'ADD_PATIENT_TO_SALESMAN_SET':
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
      let newSalesmanSets = [...state.salesmanSets.filter((value) => !(action.payload === value.id))];
      return {
        ...state,
        salesmanSets: newSalesmanSets
      };
    default:
      return state
  }
}
