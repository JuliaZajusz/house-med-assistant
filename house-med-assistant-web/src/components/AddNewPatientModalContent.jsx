import Grid from "@material-ui/core/Grid";
import Fab from "@material-ui/core/Fab";
import TextField from "@material-ui/core/TextField";
import {Paper} from "@material-ui/core";
import React, {useState} from "react";
import makeStyles from "@material-ui/core/styles/makeStyles";
import {addNewPatient, getCoordinatesByAddress} from "../actions/patientActions";
import {connect} from "react-redux";
// import Select, CreatableSelect from 'react-select';
import CreatableSelect from 'react-select/creatable';

const useStyles = makeStyles(theme => ({
  vertical_scroll_box: {
    minHeight: "calc(100% - 16px)",
    // background: theme.palette.secondary.dark,
    padding: "8px",
  },
  address_coordinates_paper: {
    cursor: 'pointer',
    padding: "5px",
    margin: "5px",
  },
  text_fields__row: {
    display: "flex",
    justifyContent: "space-between",
  },
  text_field: {
    width: "100%",
  },
  multiselect: {
    marginTop: "16px",
    marginBottom: "16px",
  },
  selected: {
    background: "pink",
  },
  text_fields__row__text_field: {
    marginRight: "10px",
    width: "100%",
  },
  text_fields__row__text_field_last: {
    marginRight: 0,
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
  getCoordinatesByAddress: (addr) => dispatch(getCoordinatesByAddress(addr)),
  addNewPatient: (patient) => dispatch(addNewPatient(patient)),
});


export default connect(mapStateToProps, mapDispatchToProps)(function AddNewPatientModalContent(props) {
    const classes = useStyles();

    const [patient, setPatient] = useState({
      address: "",
      tags: []
    });

    const [addNewPatient, setAddNewPatient] = useState(
      // false
      true
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
      props.getCoordinatesByAddress(addr)
        .then((res) => {
          if (props.coordinatesByAddress.length === 1) {
            let result = props.coordinatesByAddress[0];
            setPatientCoordinates(result.geometry.location.lng, result.geometry.location.lat)
          }
        })
    };


    const setPatientCoordinates = (lng, lat) => {
      setPatient({
        ...patient,
        coordinate: {
          location: [lng, lat]
        }
      })
    };

    const addTag = (tags) => {
      let parsedTags = tags ? tags : [];
      setPatient({...patient, tags: parsedTags.map((tag) => tag.value)})
    }

    return (
      <div className={classes.vertical_scroll_box}>
        <Grid>
          {/*{addNewPatient && */}
          <Grid>
            <div className={classes.text_fields__row}>
              <TextField
                className={classes.text_fields__row__text_field}
                label="Nazwisko"
                id="lastName"
                margin="normal"
                value={patient.lastName}
                onChange={handleChange}
              />
              <TextField
                className={classes.text_fields__row__text_field + " " + classes.text_fields__row__text_field_last}
                label="Imię"
                id="firstName"
                margin="normal"
                value={patient.firstName}
                onChange={(e) => handleChange(e)}
              />
            </div>
            <TextField
              className={classes.text_field}
              label="Adres"
              id="address"
              helperText="Podaj adres pacjenta"
              margin="normal"
              value={patient.address}
              onKeyDown={handleTextFieldKeyDown}
              onChange={handleChange}
              onBlur={() => {
                searchForCoordinates(patient.address.replace(/ /g, "+"))
              }}
            />
            <Grid>
              {props.coordinatesByAddress.map((result, resultIdx) => {
                return (
                  <Paper
                    className={classes.address_coordinates_paper
                      // && (patient.coordinate && patient.coordinate.location === [result.geometry.location.lng, result.geometry.location.lat]) && classes.selected
                    }
                    key={resultIdx}
                    onClick={() => setPatientCoordinates(result.geometry.location.lng, result.geometry.location.lat)}
                  >
                    {result.formatted_address}
                    <br/>
                    {result.geometry.location.lng.toFixed(2)} {result.geometry.location.lat.toFixed(2)}
                  </Paper>
                )
              })}
            </Grid>
            <CreatableSelect
              className={classes.multiselect}
              value={patient.tags.map(tag => {
                return {value: tag, label: tag}
              })}
              onChange={(tag) => addTag(tag)}
              options={props.tags.map(tag => {
                return {value: tag.name, label: tag.name}
              })}
              isMulti={true}
              placeholder={"Tagi"}
              formatCreateLabel={(tag) => `Dodaj tag: ${tag}`}
            />
          </Grid>
          {/*}*/}
          <div style={{display: "flex", justifyContent: "flex-end"}}>
          <Fab size="small"
               variant={"extended"}
               color="primary"
               aria-label="add"
               className={classes.margin}
               disabled={addNewPatient && !patient.coordinate}
               onClick={() => {
                 addNewPatient ? createNewPatient() : setAddNewPatient(true)
               }}
          >
            Dodaj nowego pacjenta
          </Fab>
          </div>
        </Grid>
      </div>
    )
  }
)
