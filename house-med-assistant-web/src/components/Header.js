import React from 'react';
import {Toolbar} from '@material-ui/core';
import Typography from "@material-ui/core/Typography";
import makeStyles from "@material-ui/core/styles/makeStyles";
import AppBar from "@material-ui/core/AppBar";
import Chat from "./Chat";

const useStyles = makeStyles(theme => ({
  title: {
    flexGrow: 1,
  },
}));

export default function Header(props, state) {
  const classes = useStyles();

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" className={classes.title}>
          house med assistant
        </Typography>
        <Chat/>
        {/*<SockJsClient url='http://localhost:9000/chat'*/}
        {/*              // topics={['/topics/all']}*/}
        {/*              onMessage={(msg) => { console.log(msg); }}*/}
        {/*              ref={ (client) => { this.clientRef = client }} />*/}
      </Toolbar>
    </AppBar>
  );
}

