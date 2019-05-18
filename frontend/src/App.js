import React from 'react';
import {initFirebase} from "./fire";
import Dashboard from "./DashboardView";
import {Grid} from "@material-ui/core";
import Pulse from "./PulseMonitor";
import Presence from "./PresenceMonitor";

export default class App extends React.Component {

    constructor(props) {
        super(props);
        initFirebase();
    }

    render() {
        return (
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
        )
    }
}

