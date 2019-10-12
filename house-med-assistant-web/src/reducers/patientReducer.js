export default (state = {result: null}, action) => {
  console.log("patientReducer state changes");
  switch (action.type) {
    case 'SIMPLE_ACTION':
      return {
        ...state,
        result: action.payload
      };
    case 'SET_PATIENTS':
      return {
        ...state,
        patients: action.payload
      };
    case 'SET_TAGS':
      return {
        ...state,
        tags: action.payload
      };
    default:
      return state
  }
}
