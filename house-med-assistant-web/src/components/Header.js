import React from 'react';
import {Toolbar} from '@material-ui/core';
import Typography from "@material-ui/core/Typography";
import makeStyles from "@material-ui/core/styles/makeStyles";
import AppBar from "@material-ui/core/AppBar";

const useStyles = makeStyles(theme => ({
  title: {
    flexGrow: 1,
  },
}));

export default function Header(props, state) {
  const classes = useStyles();

  console.log("render Header", props, state)
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" className={classes.title}>
          house med assistant
        </Typography>
        <div>
          <form onSubmit={(e) => props.onSockSend(e)}>
            <input type="text" placeholder="Type here to chat..."/>
            <button type="submit">Send</button>
          </form>
          {
            props.messages.map((message, index) => {
              return <div key={index}>{message}</div>
            })
          }
        </div>
      </Toolbar>
    </AppBar>
  );
}

