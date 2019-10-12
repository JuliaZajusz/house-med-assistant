import {getPatientsByTags} from "../services/PatientService";
import {getAllTags} from "../services/TagService";
import {GOOGLE_API_KEY} from "../secret/secret";

export const simpleAction = () => async (dispatch) => {
  dispatch({
    type: 'SIMPLE_ACTION',
    payload: 'result_of_simple_action'
  })
};

export const getPatientsAction = (tags) => async (dispatch, getState) => {
  let response = await getPatientsByTags(tags);
  dispatch({
    type: 'SET_PATIENTS',
    payload: response.data.patients
  })
};

export const getTagsAction = (tags) => async (dispatch, getState) => {
  let response = await getAllTags();
  dispatch({
    type: 'SET_TAGS',
    payload: response.data.tags
  })
};


export const getCoordinatesByAddress = (address) => dispatch => {
  const addr = 'https://maps.google.com/maps/api/geocode/json?address=' + address + '&key=' + GOOGLE_API_KEY;
  fetch(addr)
    .then(res => res.json())
    .then(res => {
      if (res.status !== "OK") {
        throw(res);
      }
      dispatch({
        type: 'GET_COORDINATES_BY_ADDRESS',
        payload: res.results
      });
      console.log(res.results);
    })
    .catch(error => {
      console.log(error);
    });
};


// export const simpleAction = () => dispatch => {
//   console.log("tu3");
//   dispatch({
//     type: 'SIMPLE_ACTION',
//     payload: 'result_of_simple_action'
//   })
// };
//
// export const loadGroupData = (groupId) => async (dispatch) => {
//   if (groupId > 0) {
//     return dispatch({
//       type: 'LOAD_GROUP_DATA',
//       payload: getGroupData(groupId),
//     });
//   } else {
//     dispatch({
//       type: 'RESET_GROUP_DATA',
//     });
//   }
// }
//
// export const resetGroupData = () => ({
//   type: 'RESET_GROUP_DATA',
// })
