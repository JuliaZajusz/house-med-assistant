import React from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import {connect} from "react-redux";
import {Delete, Edit} from "@material-ui/icons";
import Chip from "@material-ui/core/Chip";
import {getContrastYIQ, hashCode, intToRGB} from "../utils/Utils";


const useStyles = makeStyles(theme => ({
  paper: {
    // paddingRight: "5px",
    // paddingLeft: "5px",
    padding: "5px",
    marginBottom: '8px',
    // borderRadius: 0,
  },
  patient_name: {
    margin: 0,
  },
  flexWrapNowrap: {
    flexWrap: 'nowrap',
  }
}));

const mapStateToProps = (state) => {
  return {
    // ...state,
  }
};

const mapDispatchToProps = dispatch => ({});


export default connect(mapStateToProps, mapDispatchToProps)(function PatientPaper({patient, onDelete, onEdit, onSelect}) {
  const classes = useStyles();
  return (
    <Paper
      key={patient.id}
      className={classes.paper}
      onClick={(e) => onSelect && onSelect(e)}
    >
      <Grid container
            direction="row"
            justify="space-between"
            className={classes.flexWrapNowrap}
            alignItems='center'
      >
        <div style={{fontSize: '10px', color: 'grey'}}>
          {patient.id}
        </div>
        <div>
          {onDelete && <Delete fontSize="small" onClick={() => onDelete(patient.id)}/>}
          {onEdit && <Edit style={{color: "grey", cursor: "pointer"}} fontSize="small"
                           onClick={(e) => onEdit(e, patient.id)}/>}
        </div>
      </Grid>
      <h4 className={classes.patient_name}>
        {patient.lastName} {patient.firstName}
      </h4>
      <div>
        {patient.address}
      </div>
      <div>
        {patient.coordinate.location[0]}, {patient.coordinate.location[1]}
      </div>
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
  );
})

