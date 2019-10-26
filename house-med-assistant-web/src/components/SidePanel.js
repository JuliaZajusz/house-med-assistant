import React, {useEffect} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import SalesmanSetsList from "./SalesmanSetsList";
import connect from "react-redux/es/connect/connect";
import ActualSalesmanSet from "./ActualSalesmanSet";
import {history} from "../App";
import {getSalesmanSet} from "../actions/salesmanSetActions";


const useStyles = makeStyles(theme => ({
    side_container: {
        overflow: 'hidden',
    }
}));

const mapStateToProps = (state) => {
    return {
      ...state
    }
};

const mapDispatchToProps = dispatch => ({
  getSalesmanSet: (id) => dispatch(getSalesmanSet(id))
});

export default connect(mapStateToProps, mapDispatchToProps)(function SidePanel(props) {
    const classes = useStyles();

  console.log("SidePanel props", props, history)

  useEffect(() => {
    let id = history.location.pathname.length > 0 ? history.location.pathname.substring(1) : ""
    props.getSalesmanSet(id)
  }, [])
    return (
        <Grid
            className={classes.side_container}
        >
            <ActualSalesmanSet/>
            <SalesmanSetsList/>
        </Grid>
    );
})
