export const simpleAction = () => async (dispatch) => {
  dispatch({
    type: 'SIMPLE_ACTION',
    payload: 'result_of_simple_action'
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
