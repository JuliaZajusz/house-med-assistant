import React, {useEffect} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import SalesmanSetsList from "./SalesmanSetsList";
import connect from "react-redux/es/connect/connect";
import ActualSalesmanSet from "./ActualSalesmanSet";
import {history} from "../App";
import {getSalesmanSet} from "../actions/salesmanSetActions";
import {Scrollbars} from "react-custom-scrollbars";


const useStyles = makeStyles(theme => ({
    side_container: {
        overflow: 'hidden',
      // paddingRight:'10px',
    }
}));

const mapStateToProps = (state) => {
    return {
      mapSalesmanSet: state.salesmanSetReducer.mapSalesmanSet,
    }
};

const mapDispatchToProps = dispatch => ({
  getSalesmanSet: (id) => dispatch(getSalesmanSet(id))
});

export default connect(mapStateToProps, mapDispatchToProps)(function SidePanel(props) {
    const classes = useStyles();

  useEffect(() => {
    let id = history.location.pathname.length > 0 ? history.location.pathname.substring(1) : ""
    props.getSalesmanSet(id)
  }, []);

    return (
      <Scrollbars style={{height: "100%"}}>
        <div className={classes.side_container}>
          {/*{props.mapSalesmanSet && */}
          <ActualSalesmanSet onSockSend={(e) => props.onSockSend(e)}/>
          {/*}*/}
            <SalesmanSetsList/>
        </div>
      </Scrollbars>
    );
})
