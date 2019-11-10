import React, {useEffect} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import {connect} from "react-redux";
import {getPatientsAction, showModal} from "../actions/patientActions";
import {addPatientToSalesmanSet} from "../actions/salesmanSetActions";
import PatientPaper from "./PatientPaper";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Button from "@material-ui/core/Button";
import {Scrollbars} from "react-custom-scrollbars";


const useStyles = makeStyles(theme => ({
  vertical_scroll_box_container: {
    // position: 'relative',
    flex: "1 1 auto",
    height: '0px',

  },
  side_panel__patients_list: {
    background: theme.palette.secondary.lightMedium,
    padding: "5px",
    paddingRight: "13px",
  },
  patient_name: {
    margin: 0,
  },
  paragraph: {
    marginTop: 0,
  },
  button_container: {
    marginBottom: "5px",
    display: "flex",
    justifyContent: "flex-end",
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
  addPatientToSalesmanSet: (patient) => dispatch(addPatientToSalesmanSet(patient)),
  showModal: (action) => dispatch(showModal(action)),
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

  const showModal = (e) => {
    console.log("showModal");
    props.showModal("add")
  };

  return (
    <div
      className={classes.vertical_scroll_box_container}>
      <Scrollbars style={{height: "100%"}}>
        <div className={classes.side_panel__patients_list}>
          <Grid item className={classes.button_container}>
            <ButtonGroup size="small" aria-label="small outlined button group">
              <Button onClick={(e) => showModal(e)}>Dodaj pacjenta</Button>
              {/*<Button>Two</Button>*/}
              {/*<Button>Three</Button>*/}
            </ButtonGroup>
          </Grid>

          {props.patients && props.patients.map((patient) => {
            return <PatientPaper patient={patient}
                                 key={patient.id}
                                 onEdit={(e, id) => editPatient(e, id)}
                                 onSelect={(e) => {
                                   e.stopPropagation();
                                   props.addPatientToSalesmanSet(patient)
                                 }}/>
          })
          }
        </div>
      </Scrollbars>
    </div>
  );
})

