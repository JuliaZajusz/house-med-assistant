import React, {useEffect} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import {connect} from "react-redux";
import {getPatientsAction} from "../actions/patientActions";
import {addPatientToSalesmanSet} from "../actions/salesmanSetActions";
import PatientPaper from "./PatientPaper";


const useStyles = makeStyles(theme => ({
  side_panel__patients_list__container: {
    // position: 'relative',
    flex: "1 1 auto",
    height: '0px',

  },
  side_panel__patients_list: {
    background: "pink",
    padding: "5px",
    paddingBottom: 0,
    height: '100%',
    flexWrap: 'nowrap',
    overflowY: 'scroll',
  },
  patient_paper: {
    background: "white",
    padding: "5px",
    marginBottom: "5px"
  },
  patient_name: {
    margin: 0,
  },
  paragraph: {
    marginTop: 0,
  }
}));

const mapStateToProps = (state) => {
  return {
    ...state,
    patients: state.patientReducer.patients,
  }
};

const mapDispatchToProps = dispatch => ({
  getPatientsAction: (text, tags) => dispatch(getPatientsAction(text, tags)),
  addPatientToSalesmanSet: (coordinate) => dispatch(addPatientToSalesmanSet(coordinate))
});


export default connect(mapStateToProps, mapDispatchToProps)(function PatientsList(props) {
  const classes = useStyles();

  useEffect(() => {
    props.getPatientsAction("")
  }, []);

  const editPatient = (e, id) => {
    e.stopPropagation();
    //TODO
  };

  return (
    <div
      className={classes.side_panel__patients_list__container}>
      <Grid
        item
        container
        direction="column"
        justify="flex-start"
        alignItems='stretch'
        className={classes.side_panel__patients_list}
      >
        {props.patients && props.patients.map((patient) => {
          return <PatientPaper patient={patient} onEdit={(e, id) => editPatient(e, id)}/>
        })
        }
      </Grid>
    </div>
  );
})

