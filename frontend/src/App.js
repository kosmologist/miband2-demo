import React, {Fragment} from 'react';
import {initFirebase} from "./fire";
import Dashboard from "./DashboardView";
import {AppBar, Grid, IconButton, Toolbar, Typography} from "@material-ui/core";
import Pulse from "./PulseMonitor";
import Presence from "./PresenceMonitor";
import {AndroidOutlined, FitnessCenterOutlined} from "@material-ui/icons";
import {createMuiTheme, MuiThemeProvider} from "@material-ui/core/styles/";

export default class App extends React.Component {

    constructor(props) {
        super(props);
        initFirebase();
    }

    showSettingsModal = () => {
        console.log('Showing settings modal')
    }

    render() {
        return (
            <Fragment>
                <AppBar style={{backgroundColor: '#ffffff'}} position={"static"}>
                    <Toolbar>
                        <IconButton><FitnessCenterOutlined/></IconButton>
                        <Typography variant={"h6"} style={{flexGrow: 1}}>Demo</Typography>
                        <IconButton onClick={this.showSettingsModal}><AndroidOutlined/></IconButton>
                    </Toolbar>
                </AppBar>
                <Grid container>
                    <Grid item xs={2}>
                        <Presence/>
                    </Grid>
                    <Grid item xs={8}>
                        <Dashboard/>
                    </Grid>
                    <Grid item xs={2}>
                        <Pulse/>
                    </Grid>
                </Grid>
            </Fragment>
        )
    }
}

