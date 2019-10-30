import React, {useEffect, useState} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import InputBase from "@material-ui/core/InputBase";
import SearchIcon from '@material-ui/icons/Search';
import {fade} from "@material-ui/core/styles";
import PatientsList from "./PatientsList";
import Chip from "@material-ui/core/Chip";
import {getContrastYIQ, hashCode, intToRGB} from "../utils/Utils";
import {
  getCoordinatesByAddress,
  getPatientsAction,
  getTagsAction,
  setActiveTagsAction,
  showModal
} from "../actions/patientActions";
import {connect} from "react-redux";
import TransisionModal from "./TransisionModal";
import {Scrollbars} from 'react-custom-scrollbars';


const useStyles = makeStyles(theme => ({
  side_container: {
    overflow: 'hidden',
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
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
    border: '1px solid grey',
    overflow: 'hidden',
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
  horizontal_scroll_box_container: {
    overflowX: "scroll",
  },
  horizontal_scroll_box: {
    display: "flex",
    flexWrap: "noWrap",
  },
}));

const mapStateToProps = (state) => {
  return {
    ...state,
    tags: state.patientReducer.tags,
    activeTags: state.patientReducer.activeTags,
    coordinatesByAddress: state.patientReducer.coordinatesByAddress,
    patientModal: state.patientReducer.modal,
  }
};

const mapDispatchToProps = dispatch => ({
  getTagsAction: () => dispatch(getTagsAction()),
  getCoordinatesByAddress: (addr) => dispatch(getCoordinatesByAddress(addr)),
  setActiveTagsAction: (activeTags) => dispatch(setActiveTagsAction(activeTags)),
  getPatientsAction: (text, tags) => dispatch(getPatientsAction(text, tags)),
  showModal: (action) => dispatch(showModal(action)),
});


export default connect(mapStateToProps, mapDispatchToProps)(function PatientsPanel(props) {
  const classes = useStyles();

  const [searchValue, setSearchValue] = useState("");

  const search = (e) => {
    setSearchValue(e.target.value);
    props.getPatientsAction(e.target.value, props.activeTags)
  };

  const changeTagActivity = (tag) => {
    let activeTags = props.activeTags;
    activeTags.includes(tag) ? activeTags.remove(tag) : activeTags.push(tag);
    props.setActiveTagsAction(activeTags);
    props.getPatientsAction(searchValue, props.activeTags);
  };

  useEffect(() => {
    props.getTagsAction()
  }, []);

  return (
    <Grid className={classes.side_container}>
      <Grid className={classes.search_panel}>
        <div className={classes.search}>
          <div className={classes.searchIcon}>
            <SearchIcon/>
          </div>
          <InputBase
            placeholder="Szukaj pacjentów..."
            classes={{
              root: classes.inputRoot,
              input: classes.inputInput,
            }}
            inputProps={{'aria-label': 'search'}}
            onChange={(e) => search(e)}
          />
        </div>
        <Scrollbars style={{height: "40px"}}>
          <div className={classes.horizontal_scroll_box}>
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
                    onClick={() => changeTagActivity(tag.name)}
                  />
                )
              })
            }
          </div>
        </Scrollbars>
      </Grid>
      {props.patientModal.isOpen && <TransisionModal/>}
      {/*<AddNewPatient/>*/}
      <PatientsList/>
    </Grid>
  );
})
