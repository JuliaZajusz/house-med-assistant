import {getPatientsByTags} from "../services/PatientService";
import {getAllTags} from "../services/TagService";
import {GOOGLE_API_KEY} from "../../secret/secret";

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


export const getCoordinatesByAddress = () => dispatch => {
  const addr = 'http://maps.google.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA'
  const headers = {
    'Content-Type': 'application/json',
    // 'Referer': '*.[my-app].appspot.com/*',
    apiKey: GOOGLE_API_KEY
  };
  let fetchParams = {
    apiKey: GOOGLE_API_KEY
  };
  console.log("tuuuu", fetchParams);
  fetch(addr, {headers})
    .then(res => res.json())
    .then(res => {
      if (res.error) {
        throw(res.error);
      }
      console.log(res);
      // dispatch(fetchProductsSuccess(res.products);
      // return res.products;
    })
    .catch(error => {
      console.log(error);
      // dispatch(fetchProductsError(error));
    });
  //   dispatch({
  //   type: 'SIMPLE_ACTION',
  //   payload: 'result_of_simple_action'
  // })
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
