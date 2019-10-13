import React, {useEffect, useState} from 'react';
import Button from '@material-ui/core/Button';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import InputBase from "@material-ui/core/InputBase";
import SearchIcon from '@material-ui/icons/Search';
import {fade} from "@material-ui/core/styles";
import Fab from "@material-ui/core/Fab";
import PatientsList from "./PatientsList";
import Chip from "@material-ui/core/Chip";
import {getContrastYIQ, hashCode, intToRGB} from "../utils/Utils";
import {
  getCoordinatesByAddress,
  getPatientsAction,
  getTagsAction,
  setActiveTagsAction
} from "../actions/patientActions";
import {connect} from "react-redux";
import TextField from "@material-ui/core/TextField";
import {Paper} from "@material-ui/core";
import {Add} from "@material-ui/icons";


const useStyles = makeStyles(theme => ({
  side_container: {
    overflow: 'hidden',
  },
  button: {
    background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
    color: 'white',
    border: 0,
    borderRadius: 3,
    boxShadow: '0 3px 5px 2px rgba(255, 105, 135, .3)',
  },
  top_side_panel: {
    padding: '8px',
    alignItems: 'stretch',
    background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
  },
  side_panel: {
    paddingBottom: '1px',
    alignItems: 'stretch',
  },
  top_paper: {
    marginBottom: '8px',
    padding: '8px',
  },
  paper: {
    padding: '8px',
    borderRadius: 0,
  },
  search_panel: {
    padding: '8px',
    background: theme.palette.primary.main
  },
  search: {
    position: 'relative',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: fade(theme.palette.common.white, 0.15),
    '&:hover': {
      backgroundColor: fade(theme.palette.common.white, 0.25),
    },
    marginLeft: 0,
    width: '100%',
    [theme.breakpoints.up('sm')]: {
      marginLeft: theme.spacing(1),
      width: 'auto',
    },
    border: '1px solid grey',
  },
  searchIcon: {
    width: theme.spacing(7),
    height: '100%',
    position: 'absolute',
    pointerEvents: 'none',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  inputRoot: {
    color: 'inherit',
  },
  inputInput: {
    padding: theme.spacing(1, 1, 1, 7),
    transition: theme.transitions.create('width'),
    width: '100%',
    [theme.breakpoints.up('md')]: {
      width: 200,
    },
  },
}));

const mapStateToProps = (state) => {
  return {
    ...state,
    tags: state.patientReducer.tags,
    activeTags: state.patientReducer.activeTags,
    coordinatesByAddress: state.patientReducer.coordinatesByAddress,

  }
};

const mapDispatchToProps = dispatch => ({
  getTagsAction: () => dispatch(getTagsAction()),
  getCoordinatesByAddress: (addr) => dispatch(getCoordinatesByAddress(addr)),
  setActiveTagsAction: (activeTags) => dispatch(setActiveTagsAction(activeTags)),
  getPatientsAction: (text, tags) => dispatch(getPatientsAction(text, tags)),
});


export default connect(mapStateToProps, mapDispatchToProps)(function PatientsPanel(props) {
  const classes = useStyles();

  const [value, setValue] = useState("");

  const [addNewPatient, setAddNewPatient] = useState(
    false
    // true
  );

  let searchValue = "";

  const search = (e) => {
    searchValue = e.target.value;
    props.getPatientsAction(searchValue, props.activeTags)
  };

  const changeTagActivity = (tag) => {
    let activeTags = props.activeTags;
    activeTags.includes(tag) ? activeTags.remove(tag) : activeTags.push(tag);
    props.setActiveTagsAction(activeTags);
    props.getPatientsAction(searchValue, props.activeTags);
  };

  const createNewPatient = () => {
    setAddNewPatient(true)
  };

  const handleChange = (e) => {
    setValue(e.target.value)
  }

  const handleTextFieldKeyDown = event => {
    switch (event.key) {
      case 'Enter':
        console.log(value);
        searchForCoordinates(value.replace(/ /g, "+"))
        // call corresponding handler
        break
      case 'Escape':
        // etc...
        break
      default:
        break
    }
  };

  const searchForCoordinates = (addr) => {
    props.getCoordinatesByAddress(addr);
  }

  useEffect(() => {
    props.getTagsAction()
  }, []);


  return (
    <Grid
      className={classes.side_container}
    >
      <Grid className={classes.search_panel}>
        <div className={classes.search}>
          <div className={classes.searchIcon}>
            <SearchIcon/>
          </div>
          <InputBase
            placeholder="Szukaj zapisanych pacjentów..."
            classes={{
              root: classes.inputRoot,
              input: classes.inputInput,
            }}
            inputProps={{'aria-label': 'search'}}
            onChange={(e) => search(e)}
          />
        </div>
        {
          props.tags && props.tags.map((tag) => {
            let backgroundColor = '#' + intToRGB(hashCode(tag.name));
            return (
              <Chip
                key={tag.id}
                style={{
                  background: backgroundColor,
                  color: getContrastYIQ(backgroundColor),
                  margin: '5px',
                  opacity: !props.activeTags.includes(tag.name) ? 0.5 : 1,
                }}
                label={tag.name}
                size="small"
                // disabled={!props.activeTags.includes(tag.name)}
                onClick={() => changeTagActivity(tag.name)}
              />
            )
          })
        }
      </Grid>
      <Grid className={classes.search_panel}>
        <Button
          size="small"
          // variant="outlined"
          variant="contained"
          color={"secondary"}
          onClick={() => {
            createNewPatient()
          }}>
          Dodaj nowego pacjenta
        </Button>
        <Fab size="small"
             color="secondary"
             aria-label="add"
             className={classes.margin}
             onClick={() => {
               createNewPatient()
             }}
        >
          <Add/>
        </Fab>
        {addNewPatient && <Grid>
          <TextField
            label="Normal"
            id="margin-normal"
            helperText="Some important text"
            margin="normal"
            value={value}
            onKeyDown={handleTextFieldKeyDown}
            onChange={handleChange}
          />
          <Grid>
            {props.coordinatesByAddress.map((result, resultIdx) => {
              return (
                <Paper
                  key={resultIdx}
                >
                  {result.formatted_address}
                  <br/>
                  {result.geometry.location.lng} {result.geometry.location.lat}
                </Paper>
              )
            })}
          </Grid>
        </Grid>}
      </Grid>

      {/*<Grid*/}
      {/*    item*/}
      {/*    container*/}
      {/*    direction="column"*/}
      {/*    justify="center"*/}
      {/*    alignItems="center"*/}
      {/*    className={classes.side_panel}*/}
      {/*>*/}
      {/*</Grid>*/}

      <PatientsList/>
    </Grid>
  );
})
