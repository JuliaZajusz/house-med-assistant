import Grid from "@material-ui/core/Grid";
import Fab from "@material-ui/core/Fab";
import {Add} from "@material-ui/icons";
import TextField from "@material-ui/core/TextField";
import {Paper} from "@material-ui/core";
import React, {useState} from "react";
import makeStyles from "@material-ui/core/styles/makeStyles";
import {addNewPatient, getCoordinatesByAddress} from "../actions/patientActions";
import {connect} from "react-redux";

const useStyles = makeStyles(theme => ({
  search_panel: {
    padding: '8px',
    background: theme.palette.secondary.dark
  },
  address_coordinates_paper: {
    cursor: 'pointer',
    padding: "5px",
    margin: "5px",
  }
}));

const mapStateToProps = (state) => {
  return {
    tags: state.patientReducer.tags,
    activeTags: state.patientReducer.activeTags,
    coordinatesByAddress: state.patientReducer.coordinatesByAddress,

  }
};

const mapDispatchToProps = dispatch => ({
  // getTagsAction: () => dispatch(getTagsAction()),
  getCoordinatesByAddress: (addr) => dispatch(getCoordinatesByAddress(addr)),
  addNewPatient: (patient) => dispatch(addNewPatient(patient)),
});


export default connect(mapStateToProps, mapDispatchToProps)(function AddNewPatient(props) {
    const classes = useStyles();

    const [patient, setPatient] = useState({
      address: ""
    });

    const [addNewPatient, setAddNewPatient] = useState(
      false
      // true
    );

    const createNewPatient = () => {
      props.addNewPatient(patient)
      setAddNewPatient(false)
    };

    const handleChange = (e) => {
      setPatient({...patient, [e.target.id]: e.target.value})
    }

    const handleTextFieldKeyDown = event => {
      switch (event.key) {
        case 'Enter':
          console.log(patient);
          searchForCoordinates(patient.address.replace(/ /g, "+"))
          // call corresponding handler
          break;
        case 'Escape':
          // etc...
          break;
        default:
          break
      }
    };


    const searchForCoordinates = (addr) => {
      props.getCoordinatesByAddress(addr);
    };


    const setPatientCoordinates = (lng, lat) => {
      setPatient({
        ...patient,
        coordinate: {
          location: [lng, lat]
        }
      })
    };

    return (
      <Grid className={classes.search_panel}>
        {addNewPatient && <Grid>
          <TextField
            label="Nazwisko"
            id="lastName"
            margin="normal"
            value={patient.lastName}
            onChange={handleChange}
          />
          <TextField
            label="Imię"
            id="firstName"
            margin="normal"
            value={patient.firstName}
            onChange={(e) => handleChange(e)}
          />
          <TextField
            label="Adres"
            id="address"
            helperText="Podaj adres pacjenta"
            margin="normal"
            value={patient.address}
            onKeyDown={handleTextFieldKeyDown}
            onChange={handleChange}
          />
          <Grid>
            {props.coordinatesByAddress.map((result, resultIdx) => {
              return (
                <Paper
                  className={classes.address_coordinates_paper}
                  key={resultIdx}
                  onClick={() => setPatientCoordinates(result.geometry.location.lng, result.geometry.location.lat)}
                >
                  {result.formatted_address}
                  <br/>
                  {result.geometry.location.lng} {result.geometry.location.lat}
                </Paper>
              )
            })}
          </Grid>
        </Grid>}
        <Fab size="small"
             variant={addNewPatient ? "extended" : "round"}
             color="secondary"
             aria-label="add"
             className={classes.margin}
             onClick={() => {
               addNewPatient ? createNewPatient() : setAddNewPatient(true)
             }}
        >
          {addNewPatient ? "Dodaj nowego pacjenta"
            : <Add/>}
        </Fab>
      </Grid>
    )
  }
)
