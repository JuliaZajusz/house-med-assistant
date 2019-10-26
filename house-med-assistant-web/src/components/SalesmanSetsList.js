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
    background: "pink",
    padding: "5px",
  },
  salesmanSet_paper: {
    background: "gold",
    padding: "5px",
    marginBottom: "10px",
    width: "calc(100% - 10px)",
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
        .filter((salesmanSet) => !props.mapSalesmanSet || (props.mapSalesmanSet && props.mapSalesmanSet.id && props.mapSalesmanSet.id !== salesmanSet.id))
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
            {salesmanSet && salesmanSet.places.map((patient) => <PatientPaper patient={patient}/>)}
          </Paper>
        }
      )
      }
    </Grid>
  );
})
