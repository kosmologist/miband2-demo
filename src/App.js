import React, {Fragment} from 'react';
import {initFirebase} from "./fire";
import Dashboard from "./DashboardView";
import {AppBar, Grid, IconButton, Toolbar, Typography} from "@material-ui/core";
import Pulse from "./PulseMonitor";
import Presence from "./PresenceMonitor";
import {SettingsOutlined, FitnessCenterOutlined} from "@material-ui/icons";
import Setting from "./SettingsDialog";

export default class App extends React.Component {

    constructor(props) {
        super(props);
        initFirebase();
        this.settings = React.createRef()
    }

    showSettingsModal = () => {
        console.log('Showing settings modal')
        this.settings.current.handleOpen();
    }

    render() {
        return (
            <Fragment>
                <Setting ref={this.settings}/>
                <AppBar style={{backgroundColor: '#ffffff'}} position={"static"}>
                    <Toolbar>
                        <IconButton><FitnessCenterOutlined/></IconButton>
                        <Typography variant={"h6"} >Demo</Typography>
                        <Typography variant={"body2"} style={{marginLeft:10, flexGrow: 1}}>0.4.0</Typography>
                        <IconButton onClick={this.showSettingsModal}><SettingsOutlined/></IconButton>
                    </Toolbar>
                </AppBar>
                <Grid container>
                    <Grid item xs={2}>
                        {/*<Presence/>*/}
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

