import {
  getAllPatients,
  getAllPatientsTyText,
  getPatientsByNameAndAddressRespectingTags,
  postPatient
} from "../services/PatientService";
import {getAllTags} from "../services/TagService";
import {GOOGLE_API_KEY} from "../secret/secret";

export const getPatientsAction = (text, tags) => async (dispatch, getState) => {
  if (tags && tags.length > 0) {
    let response = await getPatientsByNameAndAddressRespectingTags(text, tags);
    dispatch({
      type: 'SET_PATIENTS',
      payload: response.data.findPatientsByTextRespectingTags
    })
  } else {
    if (text && text !== "") {
      let response = await getAllPatientsTyText(text);
      dispatch({
        type: 'SET_PATIENTS',
        payload: response.data.findPatientsByFullTextSearch
      })
    } else {
      let response = await getAllPatients();
      dispatch({
        type: 'SET_PATIENTS',
        payload: response.data.patients
      })
    }
  }
};


//nieużywane, być może przenieść do salesmanSetActions
export const getPatientByCoordinateId = (id) => async (dispatch) => {
  let response = await getPatientByCoordinateId(id);
  dispatch({
    type: 'SET_SET_PATIENTS',
    payload: response.data.patientByCoordinate
  })
}

export const getTagsAction = (tags) => async (dispatch, getState) => {
  let response = await getAllTags();
  dispatch({
    type: 'SET_TAGS',
    payload: response.data.tags
  })
};

export const setActiveTagsAction = (activeTags) => ({
  type: 'SET_ACTIVE_TAGS',
  payload: activeTags
});


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


export const addNewPatient = (patient) => dispatch => {
  postPatient(patient).then((res) => {
    dispatch({
      type: 'ADD_PATIENT',
      payload: res.data.newPatient
    });
  })
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
