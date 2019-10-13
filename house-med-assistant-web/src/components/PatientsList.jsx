import React, {useEffect} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import {connect} from "react-redux";
import {getPatientsAction} from "../actions/patientActions";
import {Edit} from "@material-ui/icons";
import Chip from "@material-ui/core/Chip";
import {getContrastYIQ, hashCode, intToRGB} from "../utils/Utils";
import {addCoordinateToSalesmanSet} from "../actions/salesmanSetActions";


const useStyles = makeStyles(theme => ({
  side_panel__patients_list: {
    background: "pink",
    padding: "5px",
    paddingBottom: 0,
    flex: "1 1 auto",
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
  addCoordinateToSalesmanSet: (coordinate) => dispatch(addCoordinateToSalesmanSet(coordinate))
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
    <Grid
      item
      container
      direction="column"
      justify="flex-start"
      alignItems='stretch'
      className={classes.side_panel__patients_list}
    >
      {props.patients && props.patients.map((patient) => {
          return <Paper
            key={patient.id}
            className={classes.patient_paper}
            onClick={() => {
              props.addCoordinateToSalesmanSet(patient.coordinate.location)
            }}
          >
            <Grid
              container
              direction="row"
              justify="space-between"
            >
              <h4 className={classes.patient_name}>
              {patient.lastName + " " + patient.firstName}
            </h4>
              {/*<IconButton aria-label="edit" className={classes.margin}*/}
              {/*            onClick={(e) => editPatient(e, patient.id)}>*/}
              <Edit style={{color: "grey", cursor: "pointer"}} fontSize="small"
                    onClick={(e) => editPatient(e, patient.id)}/>
              {/*</IconButton>*/}
            </Grid>
            <p className={classes.paragraph}>
              {patient.address}
            </p>
            <p className={classes.paragraph}>
              {patient.coordinate.location[0]}, {patient.coordinate.location[1]}
            </p>
            {patient.tags.map((tag, tagIdx) => {
              let backgroundColor = '#' + intToRGB(hashCode(tag.name));
              return (
                <Chip
                  key={tagIdx}
                  style={{
                    // background: 'linear-gradient(to right bottom, #430089, #82ffa1)'
                    background: backgroundColor,
                    color: getContrastYIQ(backgroundColor),
                    margin: '5px',
                  }}
                  label={tag.name}
                  size="small"
                />)
            })}
          </Paper>
        }
      )
      }
    </Grid>
  );
})

