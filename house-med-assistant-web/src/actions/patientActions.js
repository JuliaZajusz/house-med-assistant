import {
  getAllPatients,
  getAllPatientsTyText,
  getPatientById,
  getPatientsByNameAndAddressRespectingTags,
  postPatient,
  putPatient
} from "../services/PatientService";
import {getAllTags} from "../services/TagService";
import {GOOGLE_API_KEY} from "../secret/secret";
import {getSalesmanSet} from "./salesmanSetActions";
import {history} from "../App";

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

export const getPatient = (id) => (dispatch, getState) => {
  const state = getState();
  console.log(state);
  getPatientById(id).then((res) => {
    console.log(res.data.getPatientById);
    dispatch({
      type: 'SET_PATIENT',
      payload: res.data.getPatientById
    })
  })

}


//nieużywane, być może przenieść do salesmanSetActions
export const getPatientByCoordinateId = (id) => async (dispatch) => {
  let response = await getPatientByCoordinateId(id);
  dispatch({
    type: 'SET_SET_PATIENTS',
    payload: response.data.patientByCoordinate
  })
}

export const getTagsAction = () => async (dispatch) => {
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

export const cleanCoordinates = () => dispatch => {
  dispatch({
    type: 'SET_COORDINATES_BY_ADDRESS',
    payload: []
  });
};

export const getCoordinatesByAddress = (address) => async dispatch => {
  const addr = 'https://maps.google.com/maps/api/geocode/json?address=' + address + '&key=' + GOOGLE_API_KEY;
  let result = [];
  await fetch(addr)
    .then(res => res.json())
    .then(res => {
      if (res.status !== "OK") {
        throw(res);
      }
      dispatch({
        type: 'SET_COORDINATES_BY_ADDRESS',
        payload: res.results
      });
      result = res.results;
    })
    .catch(error => {
      console.log(error);
    });
  return result;
};


export const addNewPatient = (patient) => (dispatch) => {
  postPatient(patient).then((res) => {
    dispatch({
      type: 'ADD_PATIENT',
      payload: res.data.newPatient
    });
    dispatch(hideModal());
    dispatch(getTagsAction());
  })
};

export const updatePatient = (patient) => (dispatch) => {
  putPatient(patient).then((res) => {
    dispatch({
      type: 'UPDATE_PATIENT',
      payload: res.data.updatePatient
    });
    dispatch(hideModal());
    dispatch(getTagsAction());
    let id = history.location.pathname.length > 0 ? history.location.pathname.substring(1) : "";
    if (id !== "") {
      dispatch(getSalesmanSet(id));
    }
  })
};

export const showModal = (action) => dispatch => {
  dispatch({
    type: 'SHOW_MODAL',
    payload: action
  })
};

export const hideModal = () => dispatch => {
  dispatch({
    type: 'HIDE_MODAL',
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
