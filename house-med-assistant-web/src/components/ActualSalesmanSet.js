import React from 'react';
import Button from '@material-ui/core/Button';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import {fade} from "@material-ui/core/styles";
import Fab from "@material-ui/core/Fab";
import connect from "react-redux/es/connect/connect";
import {addNewSalesmanSet, setSalesmanSet} from "../actions/salesmanSetActions";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import PatientPaper from "./PatientPaper";


const useStyles = makeStyles(theme => ({
  top_side_panel: {
    padding: '5px',
    paddingRight: '13px',
    alignItems: 'stretch',
    background: `linear-gradient(45deg, ${theme.palette[theme.themeMotive].primary.light} 30%, ${theme.palette[theme.themeMotive].primary.broken} 90%)`,
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
  salesmanset_id: {
    flexWrap: "nowrap",
    marginTop: "5px",
    marginBottom: "5px",
  },
  button_container: {
    marginBottom: "5px",
    display: "flex",
    justifyContent: "flex-end",
  },
}));

const mapStateToProps = (state) => {
  return {
    mapSalesmanSet: state.salesmanSetReducer.mapSalesmanSet,
  }
};

const mapDispatchToProps = dispatch => ({
  addNewSalesmanSet: () => dispatch(addNewSalesmanSet()),
  setSalesmanSet: () => dispatch(setSalesmanSet()),
});

export default connect(mapStateToProps, mapDispatchToProps)(function ActualSalesmanSet(props) {
  const classes = useStyles();


  const calculatePath = () => {
    props.addNewSalesmanSet()
      .then(() =>
        props.onSockSend({target: [{value: props.mapSalesmanSet.id}]})
      )
  };
  return (
    <Grid
      item
      container
      direction="column"
      justify="center"
      alignItems="center"
      className={classes.top_side_panel}
    >
      <Grid item className={classes.button_container}>
          <ButtonGroup size="small" aria-label="small outlined button group">
            <Button
              // className={classes.outlined_button}
              onClick={() => props.setSalesmanSet()}>Wyczyść</Button>
            {/*<Button>Two</Button>*/}
            {/*<Button>Three</Button>*/}
          </ButtonGroup>
        </Grid>
      {props.mapSalesmanSet && props.mapSalesmanSet.id &&
      <Grid container
            direction="row"
            justify="space-between"
            className={classes.salesmanset_id}
            alignItems='center'
      >
        <div style={{fontSize: '10px', color: 'grey'}}>
          {props.mapSalesmanSet.id}
        </div>
      </Grid>
      }
      {props.mapSalesmanSet && props.mapSalesmanSet.places && props.mapSalesmanSet.places.map((place) =>
        <PatientPaper patient={place} onDelete={(id) => {
          console.log("onDelete", id);
          return true
        }}/>)}
      <Fab
        variant="extended"
        size="small"
        color="primary"
        aria-label="add"
        onClick={calculatePath}
      >
        Wylicz trasę
      </Fab>
    </Grid>
  );
})
