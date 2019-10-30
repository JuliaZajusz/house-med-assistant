import {
  deleteSalesmanSet,
  findSalesmanSetById,
  getAllSalesmanSets,
  postSalesmanSet,
  putSalesmanSet,
  upgradeSalesmanSet
} from "../services/SalesmanSetService";
import {history} from "../App";

export const getSalesmanSet = (salesmanSetId) => dispatch => {
  if (salesmanSetId != null && salesmanSetId !== "") {
    findSalesmanSetById(salesmanSetId).then(res => {
        dispatch({
          type: 'SET_SALESMAN_SET',
          payload: res.data.findSalesmanSetById
        });
      }
    )
  }
};

export const addPatientToSalesmanSet = (patient) => (dispatch, getState) => {
  const state = getState();
  const mapSalesmanSet = state.salesmanSetReducer.mapSalesmanSet;
  const patients = (mapSalesmanSet && mapSalesmanSet.places) ? mapSalesmanSet.places : [];
  if (!patients.includes(patient)) {
    dispatch({
      type: 'ADD_PATIENT_TO_SALESMAN_SET',
      payload: patient
    });
  }
};

export const setSalesmanSet = (salesmanSet) => dispatch => {
  history.push({
    pathname: `/${(salesmanSet && salesmanSet.id) ? salesmanSet.id : ""}`
  });
  dispatch({
    type: 'SET_SALESMAN_SET',
    payload: salesmanSet
  });
};


export const addNewSalesmanSet = () => async (dispatch, getState) => {
  const mapSalesmanSet = getState().salesmanSetReducer.mapSalesmanSet;
  if (mapSalesmanSet.id != null) {
    return putSalesmanSet(mapSalesmanSet)
      .then(response => {
        dispatch(setSalesmanSet(response.data.updateSalesmanSet));
      })
  }
  await postSalesmanSet(mapSalesmanSet)
    .then(response => {
        dispatch(setSalesmanSet(response.data.newSalesmanSet));
      }
    )
};

// export const addNewSalesmanSet = () => (dispatch, getState) => {
//   const mapSalesmanSet = getState().salesmanSetReducer.mapSalesmanSet;
//   console.log("addNewSalesmanSet mapSalesmanSet", mapSalesmanSet);
//   postSalesmanSet(mapSalesmanSet)
//     .then(response => {
//       if (response.data.updateSalesmanSet) {
//         dispatch(repeat(response.data.updateSalesmanSet));
//       }
//       if (response.data.newSalesmanSet) {
//         dispatch(repeat(response.data.newSalesmanSet));
//       }
//     })
// };

const repeat = (set) => dispatch => {
  dispatch(setSalesmanSet(set));
  upgradeSalesmanSet(set.id, 2, 20, 20)
    .then((response) => {
      //TODO jeśli stan się zmienił, doszedł nowy punkt to nie update'uj, przerwij request
      dispatch(setSalesmanSet(response.data.upgradeSalesmanSet))
    })
};

export const loadAllSalesmanSets = () => dispatch => {
  getAllSalesmanSets()
    .then((res) => {
      dispatch({
        type: 'GET_SALESMAN_SETS',
        payload: res.data.salesmanSets
      })
    })
};

export const removeSalesmanSet = (id) => dispatch => {
  deleteSalesmanSet(id).then((res) => {
      console.log("res: ", res);
      dispatch({
        type: 'REMOVE_SALESMAN_SET',
        payload: res ? id : ""
      })
    }
  )
};
