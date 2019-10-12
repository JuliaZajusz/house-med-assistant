import {addCoordinate} from "../services/DefaultService";
import {postSalesmanSet, upgradeSalesmanSet} from "../services/SalesmanSetService";
import {history} from "../App";

export const addCoordinateToSalesmanSet = (coordinate) => dispatch => {
  // console.log(coordinate)
  //nie wiem, ale tu chyba nie trzeba pchać tego na backend jeszcze
  addCoordinate(coordinate)
    .then(result => {
        dispatch({
          type: 'ADD_COORDINATE_TO_SALESMAN_SET',
          payload: result.data.newCoordinate
        });
      }
    );
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

export const addNewSalesmanSet = () => (dispatch, getState) => {
  const mapSalesmanSet = getState().salesmanSetReducer.mapSalesmanSet;
  postSalesmanSet(mapSalesmanSet)
    .then(response => {
      if (response.data.updateSalesmanSet) {
        repeat(response.data.updateSalesmanSet);
      }
      if (response.data.newSalesmanSet) {
        repeat(response.data.newSalesmanSet);
      }
    })
};

const repeat = (set) => {
  // history.push({
  //     pathname: `/${set.id}`
  // });
  setSalesmanSet(set);
  upgradeSalesmanSet(set.id, 2, 20, 20)
    .then((response) => {
      //TODO jeśli stan się zmienił, doszedł nowy punkt to nie update'uj, przerwij request
      setSalesmanSet(response.data.upgradeSalesmanSet)
    })
}

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
