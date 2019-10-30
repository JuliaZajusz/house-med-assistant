import React, {Component} from 'react';
import './App.css';
import MapWrapper from "./components/Map";
import Grid from "@material-ui/core/Grid";
import {createMuiTheme} from '@material-ui/core/styles';
import purple from '@material-ui/core/colors/purple';
import ThemeProvider from "@material-ui/styles/ThemeProvider";
import SidePanel from "./components/SidePanel";
import {BrowserRouter} from "react-router-dom";
import {createBrowserHistory} from 'history';
import PatientsPanel from "./components/PatientsPanel";
import Header from "./components/Header";
import SockJS from "sockjs-client"
import {withStyles} from '@material-ui/styles';

export const history = createBrowserHistory();

const theme = createMuiTheme({
    palette: {
        primary: purple,
        secondary: {
            main: '#f44336',
            dark: '#424242',
            light: '#fefefe',
          lightMedium: '#eeeeee',
          lightMediumBorder: '#c0c0c0',
        },
      background: '#ffffff',
    },
    shape: {
        borderRadius: '3px',
    }
});

const useStyles = theme => ({
    app: {
        height: '100%',
        maxHeight: '100%',
        width: '100%',
        maxWidth: '100%',
        display: 'flex',
        flexDirection: 'column',
    },
    main_layout_box: {
        flexGrow: 1,
        height: '100%',
        maxHeight: '100%',
    },
    title: {
        flexGrow: 1,
    },
    side_container_container: {
        height: '100%',
        maxHeight: '100%',
        overflowY: 'auto',
        borderRight: "1px solid " + theme.palette.secondary.lightMediumBorder,
        // boxShadow: "6px 0px 5px 0px rgba(0,0,0,0.75)"
    }
});

class App extends Component {
    // classes = useStyles();
  // classes = {};

    constructor(props) {
        super(props);
        this.state = {
            messages: ["lala", "aa"]
        }


        const sock = new SockJS('http://localhost:9000/chat');

        sock.onopen = () => {
            console.log("onopen")
        }


        sock.onmessage = e => {
            let data = JSON.parse(e.data).data
            this.setState({messages: [data, ...this.state.messages]});
            console.log("onmessage", e.data, data)
        };

        sock.onclose = () => {
            console.log("onclose")
        }


        this.sock = sock;
    }

    onSockSend = (e) => {
        // e.preventDefault();
        console.log("handleFormSubmit")
        this.sock.send(JSON.stringify({type: "jul", data: e.target[0].value}));
    }


    render() {
      const {classes} = this.props;
        console.log("render App");
        return (
          <div className={classes.app}>
          <BrowserRouter history={history}>
              <ThemeProvider theme={theme}>
                  <Header messages={this.state.messages} onSockSend={(e) => this.onSockSend(e)}/>
                  <Grid container
                        direction="row"
                        justify="center"
                        className={classes.main_layout_box}
                        alignItems='stretch'
                  >
                      <Grid item xs={3}
                            flexgrow={1}
                            className={classes.side_container_container}
                      >
                          <SidePanel onSockSend={(e) => this.onSockSend(e)}/>
                      </Grid>
                      <Grid item xs={6}>
                          <MapWrapper/>
                      </Grid>
                      <Grid item xs={3}
                            flexgrow={1}
                            className={classes.side_container_container}
                      >
                          <PatientsPanel/>
                      </Grid>
                  </Grid>
              </ThemeProvider>
          </BrowserRouter>
      </div>
        )
    }
}

export default withStyles(useStyles(theme))(App);
