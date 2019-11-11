import React, {useEffect, useState} from 'react';
import Button from '@material-ui/core/Button';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Fab from "@material-ui/core/Fab";
import connect from "react-redux/es/connect/connect";
import {addNewSalesmanSet, changeSalesmanSetName, setSalesmanSet} from "../actions/salesmanSetActions";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import PatientPaper from "./PatientPaper";
import TextField from "@material-ui/core/TextField";


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
  text_field: {
    marginTop: 0,
  }
}));

const mapStateToProps = (state) => {
  return {
    mapSalesmanSet: state.salesmanSetReducer.mapSalesmanSet,
  }
};

const mapDispatchToProps = dispatch => ({
  addNewSalesmanSet: () => dispatch(addNewSalesmanSet()),
  setSalesmanSet: (salesmanSet) => dispatch(setSalesmanSet(salesmanSet)),
  changeSalesmanSetName: (name) => dispatch(changeSalesmanSetName(name)),
});

export default connect(mapStateToProps, mapDispatchToProps)(function ActualSalesmanSet(props) {
  const classes = useStyles();

  const [name, setName] = useState(() => {
    return props.mapSalesmanSet && props.mapSalesmanSet.name
      ? props.mapSalesmanSet.name : ""
  });

  const [isNameEditable, setIsNameEditable] = useState(true);

  const handleNameChange = (e) => {
    setName(e.target.value)
  };

  const calculatePath = () => {
    // if(props.mapSalesmanSet.id) {
    //   props.onSockSend({target: [{value: props.mapSalesmanSet.id}]})
    // } else {
    props.addNewSalesmanSet()
      .then((res) => {
          //   console.log("res", res);
          props.onSockSend({target: [{value: props.mapSalesmanSet.id}]})
        }
      )
    // }

  };

  useEffect(() => {
      console.log("id się zmieniło", props.mapSalesmanSet.id)
      // props.onSockSend({target: [{value: props.mapSalesmanSet.id}]})
    }, [props.mapSalesmanSet.id]
  )

  const handleTextFieldKeyDown = event => {
    if (event.key === 'Enter') {
      saveName(event.target.value);
    }
  };

  React.useEffect(() => {
    if (props.mapSalesmanSet) {
      setName(props.mapSalesmanSet.name);
    }
  }, [props.mapSalesmanSet])

  const saveName = (newName) => {
    setIsNameEditable(true);
    props.changeSalesmanSetName(newName)
  };

  const onDelete = (id) => {
    let salesmanSet = props.mapSalesmanSet;
    salesmanSet.places = [...salesmanSet.places.filter((place) => {
      return place.id !== id
    })];
    console.log(salesmanSet);
    props.addNewSalesmanSet(salesmanSet);
    return true
  };

  const clear = () => {
    props.setSalesmanSet();
    props.onSockSendStop(props.mapSalesmanSet.id);
  }

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
            {/*<Button onClick={() => setIsNameEditable(true)}>Edytuj</Button>*/}
            <Button
              // className={classes.outlined_button}
              onClick={() => clear()}>Wyczyść</Button>
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
      {props.mapSalesmanSet && props.mapSalesmanSet.id &&
      <TextField
        className={classes.text_field}
        label="Nazwa"
        id="name"
        margin="normal"
        value={name}
        onChange={handleNameChange}
        onKeyDown={handleTextFieldKeyDown}
        onBlur={() => saveName(name)}
        disabled={!isNameEditable}
      />

      }
      {props.mapSalesmanSet && props.mapSalesmanSet.places && props.mapSalesmanSet.places.map((place) =>
        <PatientPaper key={place.id + place.coordinate.id} patient={place} onDelete={isNameEditable && onDelete}
                      isDeletable={isNameEditable}/>)}
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
