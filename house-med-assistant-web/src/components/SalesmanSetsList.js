import React, {useEffect, useState} from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import DeleteIcon from '@material-ui/icons/Delete';
import {deleteSalesmanSet, getAllSalesmanSets} from "../services/SalesmanSetService";
import IconButton from "@material-ui/core/IconButton";


const useStyles = makeStyles(theme => ({
  side_panel__salesman_list: {
    background: "pink",
    padding: "5px",
  },
  salesmanSet_paper: {
    background: "gold",
    padding: "5px",
    marginBottom: "10px"
  }
}));

export default function SalesmanSetsList(props) {
  const classes = useStyles();

  const [salesmanSets, setSalesmanSets] = useState(
    []
  );

  useEffect(() => {
    // Your code here
    console.log("useEffect SalesmanSetsList")
    getAllSalesmanSets()
      .then(result => {
          setSalesmanSets(result.data.salesmanSets);
          // console.log(result)
          // let set = result.data.salesmanSets[1]
          // setSalesmanSet({...set, places: [...salesmanSet.places, ...set.places]})
        }
      )
  });

  console.log("salesmanSets", salesmanSets)

  const deleteDalesmanSet = (e, id) => {
    e.stopPropagation();
    deleteSalesmanSet(id).then((res) => {
        if (res.data.deleteSalesmanSet) {
          setSalesmanSets(salesmanSets.filter((salesmanSet) => salesmanSet.id != id))
        }
        return res
      }
    )
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

      {salesmanSets && salesmanSets.map((salesmanSet) => {
          return <Paper
            key={salesmanSet.id}
            className={classes.salesmanSet_paper}
            onClick={() => props.onSetSalesmanSet(salesmanSet)}
          >
            <h4>
              {salesmanSet.id}
              <IconButton aria-label="delete" className={classes.margin}
                          onClick={(e) => deleteDalesmanSet(e, salesmanSet.id)}>
                <DeleteIcon fontSize="small"/>
              </IconButton>
            </h4>
            {salesmanSet && salesmanSet.places
              .map((place) => <Paper
                  key={place.id}
                  className={classes.paper}
                >
                  <p>
                    {place.id}
                  </p>
                  <p>
                    {place.name}: {place.location[0]}, {place.location[1]}
                  </p>
                </Paper>
              )}
          </Paper>
        }
      )
      }
    </Grid>
  )
    ;
}
