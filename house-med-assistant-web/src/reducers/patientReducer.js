export default (state = {result: null}, action) => {
  console.log("patientReducer state changes");
  switch (action.type) {
    case 'SIMPLE_ACTION':
      return {
        result: action.payload
      };
    default:
      return state
  }
}
