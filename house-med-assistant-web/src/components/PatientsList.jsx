import React, {useEffect} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import IconButton from "@material-ui/core/IconButton";
import {connect} from "react-redux";
import {getPatientsAction} from "../actions/patientActions";
import {Edit} from "@material-ui/icons";
import Chip from "@material-ui/core/Chip";
import {getContrastYIQ, hashCode, intToRGB} from "../utils/Utils";
import {addCoordinateToSalesmanSet} from "../actions/salesmanSetActions";


const useStyles = makeStyles(theme => ({
  side_panel__salesman_list: {
    background: "pink",
    padding: "5px",
  },
  salesmanSet_paper: {
    background: "gold",
    padding: "5px",
    marginBottom: "10px"
  }
}));

const mapStateToProps = (state) => {
  return {
    ...state,
    patients: state.patientReducer.patients,
  }
};

const mapDispatchToProps = dispatch => ({
  getPatientsAction: (tags) => dispatch(getPatientsAction(tags)),
  addCoordinateToSalesmanSet: (coordinate) => dispatch(addCoordinateToSalesmanSet(coordinate))
});


export default connect(mapStateToProps, mapDispatchToProps)(function PatientsList(props) {
  const classes = useStyles();

  useEffect(() => {
    props.getPatientsAction()
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
      justify="center"
      alignItems="center"
      className={classes.side_panel__salesman_list}
    >
      {props.patients && props.patients.map((patient) => {
          return <Paper
            key={patient.id}
            className={classes.salesmanSet_paper}
            onClick={() => {
              props.addCoordinateToSalesmanSet(patient.coordinate.location)
            }}
          >
            <h4>
              {patient.lastName + " " + patient.firstName}
              <IconButton aria-label="edit" className={classes.margin}
                          onClick={(e) => editPatient(e, patient.id)}>
                <Edit fontSize="small"/>
              </IconButton>
            </h4>
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

