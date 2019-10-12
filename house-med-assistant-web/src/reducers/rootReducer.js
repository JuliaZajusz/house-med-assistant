import {combineReducers} from 'redux';
import patientReducer from './patientReducer';
import salesmanSetReducer from "./salesmanSetReducer";

export default combineReducers({
  patientReducer,
  salesmanSetReducer
});
