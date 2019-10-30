import React, {useEffect} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import {loadAllSalesmanSets, removeSalesmanSet, setSalesmanSet} from "../actions/salesmanSetActions";
import connect from "react-redux/es/connect/connect";
import PatientPaper from "./PatientPaper";
import {Delete} from "@material-ui/icons";


const useStyles = makeStyles(theme => ({
  side_panel__salesman_list: {
    background: "#ffffff",
    padding: "5px",
    paddingRight: "13px",
  },
  salesmanSet_paper: {
    background: "#eeeeee",
    padding: "5px",
    marginBottom: "10px",
    width: "calc(100% - 10px)",
    border: "1px solid #c0c0c0"
  },
  flexWrapNowrap: {
    flexWrap: "nowrap"
  },
  deleteButton: {
    cursor: "pointer"
  }
}));

const mapStateToProps = (state) => {
  return {
    salesmanSets: state.salesmanSetReducer.salesmanSets,
    mapSalesmanSet: state.salesmanSetReducer.mapSalesmanSet,
  }
};

const mapDispatchToProps = dispatch => ({
  setSalesmanSet: (salesmanSet) => dispatch(setSalesmanSet(salesmanSet)),
  getAllSalesmanSets: () => dispatch(loadAllSalesmanSets()),
  removeSalesmanSet: (id) => dispatch(removeSalesmanSet(id)),
});

export default connect(mapStateToProps, mapDispatchToProps)(function SalesmanSetsList(props) {
  const classes = useStyles();

  useEffect(() => {
    props.getAllSalesmanSets();
  }, [])


  const deleteDalesmanSet = (e, id) => {
    e.stopPropagation();
    props.removeSalesmanSet(id);
  };

  const isActualSalesmanSet = (salesmanSet) => {
    const isAnyActualSalesmanSet = props.mapSalesmanSet && props.mapSalesmanSet.id;
    return isAnyActualSalesmanSet && props.mapSalesmanSet.id === salesmanSet.id;
  }

  return (
    <Grid
      item
      container
      direction="column"
      justify="center"
      alignItems="center"
      className={classes.side_panel__salesman_list}
    >

      {props.salesmanSets && props.salesmanSets
        .filter((salesmanSet) => !isActualSalesmanSet(salesmanSet))
        .map((salesmanSet) => {
          return <Paper
            key={salesmanSet.id}
            className={classes.salesmanSet_paper}
            onClick={() => props.setSalesmanSet(salesmanSet)}
          >
            <Grid container
                  direction="row"
                  justify="space-between"
                  className={classes.flexWrapNowrap}
                  alignItems='center'
            >
              <div style={{fontSize: '10px', color: 'grey'}}>
                {salesmanSet.id}
              </div>
              <div>
                <Delete fontSize="small" className={classes.deleteButton}
                        onClick={(e) => deleteDalesmanSet(e, salesmanSet.id)}/>
              </div>
            </Grid>
            {salesmanSet.name && <p>{salesmanSet.name}</p>}
            {salesmanSet && salesmanSet.places.map((patient) => <PatientPaper patient={patient}/>)}
          </Paper>
        }
      )
      }
    </Grid>
  );
})
